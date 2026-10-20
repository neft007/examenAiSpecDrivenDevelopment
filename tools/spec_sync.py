#!/usr/bin/env python3
"""Valida que la implementación esté sincronizada con las specs.

Uso (desde la raíz del repositorio): python tools/spec_sync.py
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
USER_STORIES = ROOT / "specs/user-stories"
TRACEABILITY = ROOT / "specs/traceability.md"
MAIN = ROOT / "src/main/java"
TEST = ROOT / "src/test/java"

HTTP_METHODS = {"get", "put", "post", "delete", "patch", "head", "options", "trace"}
TEST_ANNOTATION = re.compile(r"^\s*@(Test|ParameterizedTest|RepeatedTest|TestFactory)\b")
METHOD_NAME = re.compile(r"\bvoid\s+(\w+)\s*\(")
CA_TEST_NAME = re.compile(r"^(US\d{2})_(CA\d{2})_[a-z][A-Za-z0-9]*$")
TEST_CLASS = re.compile(r"\b([A-Z][A-Za-z0-9]*Tests?)\b")

errors = []
warnings = []
info = []


def rel(path):
    return path.relative_to(ROOT).as_posix()


def test_methods():
    """Devuelve [(archivo, método)] de los métodos anotados como prueba en src/test/java."""
    found = []
    for path in sorted(TEST.rglob("*.java")):
        pending = False
        for line in path.read_text(encoding="utf-8").splitlines():
            if TEST_ANNOTATION.match(line):
                pending = True
                continue
            if pending:
                match = METHOD_NAME.search(line)
                if match:
                    found.append((path, match.group(1)))
                    pending = False
    return found


def acceptance_criteria():
    """Devuelve {'US01_CA01': archivo} con los CA de specs/user-stories/."""
    criteria = {}
    for path in sorted(USER_STORIES.glob("US[0-9][0-9]-*.md")):
        story = path.name[:4]
        for number in re.findall(r"\*\*CA-(\d{2}):\*\*", path.read_text(encoding="utf-8")):
            criteria[f"{story}_CA{number}"] = path
    return criteria


def check_operations():
    """Regla 1: cada operationId tiene un método con su nombre en un controlador de adapter.in.web."""
    controllers = [p for p in MAIN.rglob("adapter/in/web/*Controller.java")]
    try:
        contract = yaml.safe_load(CONTRACT.read_text(encoding="utf-8"))
    except (OSError, yaml.YAMLError) as exc:
        errors.append(f"No se puede leer {rel(CONTRACT)} (ejecuta spec_lint.py): {exc.__class__.__name__}")
        return 0, len(controllers)
    if not isinstance(contract, dict):
        errors.append(f"{rel(CONTRACT)} no es un documento OpenAPI (ejecuta spec_lint.py)")
        return 0, len(controllers)
    sources = {p: p.read_text(encoding="utf-8") for p in controllers}
    total = 0
    for route, item in (contract.get("paths") or {}).items():
        for method, operation in (item or {}).items():
            if method not in HTTP_METHODS or not isinstance(operation, dict):
                continue
            operation_id = operation.get("operationId")
            if not operation_id:
                continue  # spec_lint ya lo informa
            total += 1
            pattern = re.compile(rf"\b{re.escape(operation_id)}\s*\(")
            owners = [p.name for p, text in sources.items() if pattern.search(text)]
            if not owners:
                errors.append(f"{method.upper()} {route}: '{operation_id}' no está implementado en ningún controlador")
            elif len(owners) > 1:
                warnings.append(f"'{operation_id}' aparece en varios controladores: {', '.join(owners)}")
    return total, len(controllers)


def check_criteria(methods, criteria):
    """Regla 2: cada CA tiene al menos una prueba que empieza por USxx_CAyy_."""
    covered = {f"{m.group(1)}_{m.group(2)}" for _, name in methods if (m := CA_TEST_NAME.match(name))}
    for ca, path in criteria.items():
        if ca not in covered:
            errors.append(f"{ca} ({rel(path)}) no tiene ninguna prueba {ca}_...")


def check_naming(methods, criteria):
    """Regla 3: las pruebas con prefijo US siguen USxx_CAyy_descripcionEnIngles y citan un CA existente.

    Las pruebas sin prefijo (reglas transversales, recurso inexistente, restore...) se admiten
    y solo se cuentan como información.
    """
    without_prefix = []
    for path, name in methods:
        if not name.startswith("US"):
            without_prefix.append(f"{path.stem}.{name}")
            continue
        match = CA_TEST_NAME.match(name)
        if not match:
            errors.append(f"{rel(path)}: '{name}' no sigue USxx_CAyy_descripcionEnIngles")
        elif f"{match.group(1)}_{match.group(2)}" not in criteria:
            errors.append(f"{rel(path)}: '{name}' cita un criterio que no existe en specs/user-stories/")
    if without_prefix:
        info.append(f"{len(without_prefix)} pruebas sin prefijo USxx_CAyy_ (admitidas: no cubren un CA concreto)")


def check_traceability():
    """Regla 4: las clases de prueba citadas en traceability.md existen en src/test/java."""
    names = sorted(set(TEST_CLASS.findall(TRACEABILITY.read_text(encoding="utf-8"))))
    existing = {p.stem for p in TEST.rglob("*.java")}
    for name in names:
        if name not in existing:
            errors.append(f"{rel(TRACEABILITY)} cita '{name}', que no existe en {rel(TEST)}")
    return len(names)


def main():
    methods = test_methods()
    criteria = acceptance_criteria()
    operations, controllers = check_operations()
    check_criteria(methods, criteria)
    check_naming(methods, criteria)
    traced = check_traceability()

    print(f"spec_sync: {rel(CONTRACT)} ↔ {rel(MAIN)}, {rel(USER_STORIES)} ↔ {rel(TEST)}")
    for message in errors:
        print(f"  [ERROR] {message}")
    for message in warnings:
        print(f"  [WARN]  {message}")
    for message in info:
        print(f"  [INFO]  {message}")
    status = "OK" if not errors else "FALLO"
    print(f"Resultado: {status} ({operations} operaciones en {controllers} controladores, "
          f"{len(criteria)} criterios, {len(methods)} métodos de prueba, {traced} clases en traceability.md; "
          f"{len(errors)} errores, {len(warnings)} advertencias)")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
