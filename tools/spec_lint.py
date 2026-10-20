#!/usr/bin/env python3
"""Valida la coherencia interna de las specs y del contrato OpenAPI.

Uso (desde la raíz del repositorio): python tools/spec_lint.py
Devuelve 0 si no hay errores y 1 si hay al menos uno.
"""
import re
import sys
from pathlib import Path

try:
    import yaml
except ImportError:
    sys.exit("PyYAML no está instalado: pip install pyyaml")

ROOT = Path(__file__).resolve().parent.parent
CONTRACT = ROOT / "specs/api/gimnasio-openapi.yaml"
CATALOGS = ROOT / "specs/domain/catalogs.md"
LIFECYCLE = ROOT / "specs/domain/lifecycle.md"
FUNCTIONAL = ROOT / "specs/requirements/functional.md"
USER_STORIES = ROOT / "specs/user-stories"
TRACEABILITY = ROOT / "specs/traceability.md"

# Enum del contrato -> texto que identifica su sección en catalogs.md y lifecycle.md
ENUM_SECTIONS = {
    "EstadoMembresia": "membresía",
    "EstadoClase": "clase",
    "EstadoReserva": "reserva",
}
HTTP_METHODS = {"get", "put", "post", "delete", "patch", "head", "options", "trace"}
ERROR_SCHEMA_REF = "#/components/schemas/ErrorResponse"

errors = []
warnings = []


def rel(path):
    return path.relative_to(ROOT).as_posix()


def codes_by_section(path):
    """Devuelve {título de sección '## ...': [códigos en orden]} de las tablas del Markdown."""
    sections = {}
    current = None
    for line in path.read_text(encoding="utf-8").splitlines():
        if line.startswith("## "):
            current = line[3:].strip()
            sections[current] = []
        elif current and line.startswith("|"):
            # Fila de tabla con un código entre comillas invertidas (el encabezado y el separador no tienen)
            match = re.search(r"`([A-Z][A-Z_]*)`", line)
            if match:
                sections[current].append(match.group(1))
    return sections


def find_section(sections, keyword):
    for title, codes in sections.items():
        if keyword in title.lower():
            return title, codes
    return None, None


def resolve(document, ref):
    """Resuelve un JSON pointer interno ('#/a/b'); devuelve None si no existe."""
    node = document
    for part in ref[2:].split("/"):
        part = part.replace("~1", "/").replace("~0", "~")
        if not isinstance(node, dict) or part not in node:
            return None
        node = node[part]
    return node


def iter_refs(node, path="#"):
    if isinstance(node, dict):
        for key, value in node.items():
            if key == "$ref" and isinstance(value, str):
                yield path, value
            else:
                yield from iter_refs(value, f"{path}/{key}")
    elif isinstance(node, list):
        for index, value in enumerate(node):
            yield from iter_refs(value, f"{path}/{index}")


def iter_operations(contract):
    for route, item in (contract.get("paths") or {}).items():
        for method, operation in (item or {}).items():
            if method in HTTP_METHODS and isinstance(operation, dict):
                yield route, method, operation


def load_contract():
    """Regla 1: YAML válido y OpenAPI 3.x."""
    try:
        contract = yaml.safe_load(CONTRACT.read_text(encoding="utf-8"))
    except FileNotFoundError:
        errors.append(f"No existe {rel(CONTRACT)}")
        return None
    except yaml.YAMLError as exc:
        errors.append(f"{rel(CONTRACT)} no es YAML válido: {exc}")
        return None
    if not isinstance(contract, dict):
        errors.append(f"{rel(CONTRACT)} no es un documento OpenAPI (la raíz no es un objeto)")
        return None
    version = str(contract.get("openapi", ""))
    if not re.fullmatch(r"3\.\d+(\.\d+)?", version):
        errors.append(f"openapi: '{version}' no es una versión 3.x")
    for key in ("info", "paths"):
        if key not in contract:
            errors.append(f"Falta la sección obligatoria '{key}' en el contrato")
    return contract


def check_enums(contract):
    """Regla 2: enums del contrato iguales (valores y orden) a catalogs.md y lifecycle.md."""
    schemas = (contract.get("components") or {}).get("schemas") or {}
    sources = [(CATALOGS, codes_by_section(CATALOGS)), (LIFECYCLE, codes_by_section(LIFECYCLE))]
    for enum_name, keyword in ENUM_SECTIONS.items():
        schema = schemas.get(enum_name)
        if not schema or "enum" not in schema:
            errors.append(f"El contrato no define el enum {enum_name}")
            continue
        contract_values = [str(v) for v in schema["enum"]]
        for path, sections in sources:
            title, codes = find_section(sections, keyword)
            if title is None:
                errors.append(f"{rel(path)}: no hay sección para {enum_name} (se busca '{keyword}')")
            elif codes != contract_values:
                errors.append(f"{enum_name}: contrato {contract_values} ≠ {rel(path)} '{title}' {codes}")


def known_spec_ids():
    ids = set(re.findall(r"RF-\d{2}", FUNCTIONAL.read_text(encoding="utf-8")))
    ids |= {f.name[:4] for f in USER_STORIES.glob("US[0-9][0-9]-*.md")}
    return ids


def check_operations(contract):
    """Regla 3: cada operación tiene operationId y x-spec-refs (con RF y US existentes)."""
    known = known_spec_ids()
    traceability = TRACEABILITY.read_text(encoding="utf-8")
    seen = set()
    for route, method, operation in iter_operations(contract):
        where = f"{method.upper()} {route}"
        operation_id = operation.get("operationId")
        if not operation_id:
            errors.append(f"{where}: falta operationId")
        elif operation_id in seen:
            errors.append(f"{where}: operationId '{operation_id}' repetido")
        else:
            seen.add(operation_id)
            if f"| {operation_id} |" not in traceability:
                warnings.append(f"{where}: '{operation_id}' no aparece en {rel(TRACEABILITY)}")
        refs = operation.get("x-spec-refs")
        if not refs:
            errors.append(f"{where}: falta x-spec-refs")
            continue
        for ref in refs:
            if ref not in known:
                errors.append(f"{where}: x-spec-refs cita '{ref}', que no existe en specs/")


def check_error_responses(contract):
    """Regla 4: toda respuesta 4xx usa ErrorResponse (directamente o vía components/responses)."""
    for route, method, operation in iter_operations(contract):
        for status, response in (operation.get("responses") or {}).items():
            if not str(status).startswith("4"):
                continue
            where = f"{method.upper()} {route} {status}"
            if isinstance(response, dict) and "$ref" in response:
                response = resolve(contract, response["$ref"])
                if response is None:
                    continue  # la regla 5 ya informa de la referencia rota
            content = (response or {}).get("content") or {}
            schema = (content.get("application/json") or {}).get("schema") or {}
            if not content:
                errors.append(f"{where}: la respuesta no tiene cuerpo (debe usar ErrorResponse)")
            elif schema.get("$ref") != ERROR_SCHEMA_REF:
                errors.append(f"{where}: el esquema no es ErrorResponse ({schema or 'sin esquema'})")


def check_refs(contract):
    """Regla 5: todos los $ref internos resuelven; los externos no se admiten."""
    for path, ref in iter_refs(contract):
        if not ref.startswith("#/"):
            errors.append(f"{path}: $ref externo no admitido ('{ref}')")
        elif resolve(contract, ref) is None:
            errors.append(f"{path}: $ref '{ref}' no resuelve")


def main():
    contract = load_contract()
    if contract is not None:
        check_enums(contract)
        check_operations(contract)
        check_error_responses(contract)
        check_refs(contract)

    print(f"spec_lint: {rel(CONTRACT)}, {rel(CATALOGS)}, {rel(LIFECYCLE)}")
    for message in errors:
        print(f"  [ERROR] {message}")
    for message in warnings:
        print(f"  [WARN]  {message}")
    operations = len(list(iter_operations(contract))) if contract else 0
    status = "OK" if not errors else "FALLO"
    print(f"Resultado: {status} ({operations} operaciones; {len(errors)} errores, {len(warnings)} advertencias)")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
