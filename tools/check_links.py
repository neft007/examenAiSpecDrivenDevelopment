#!/usr/bin/env python3
"""Comprueba los enlaces relativos de la documentación Markdown.

Uso (desde la raíz del repositorio): python tools/check_links.py
Devuelve 0 si no hay errores y 1 si hay al menos uno.
"""
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

# Archivos que no necesitan enlaces entrantes: puntos de entrada y archivos de infraestructura.
ENTRY_POINTS = {"README.md", "CLAUDE.md", ".gitignore", ".gitattributes", "mvnw", "mvnw.cmd"}
# Prefijos que no se comprueban como posibles huérfanos: código fuente y wrapper de Maven.
IGNORED_PREFIXES = ("src/", ".mvn/")

FENCE = re.compile(r"^```.*?^```", re.M | re.S)
INLINE_CODE = re.compile(r"`[^`\n]*`")
LINK = re.compile(r"\]\(([^)\s]+)\)")
HEADING = re.compile(r"^#{1,6}\s+(.+?)\s*#*\s*$", re.M)
EXTERNAL = re.compile(r"^(https?:|mailto:)")

errors = []
warnings = []
info = []


def tracked_files():
    """Archivos versionados (git ls-files); si git no está disponible, todos menos los ocultos y target/."""
    try:
        out = subprocess.run(["git", "ls-files"], cwd=ROOT, capture_output=True, text=True, check=True).stdout
        return [ROOT / line for line in out.splitlines() if line]
    except (OSError, subprocess.CalledProcessError):
        return [p for p in ROOT.rglob("*") if p.is_file()
                and not any(part.startswith(".") or part == "target" for part in p.relative_to(ROOT).parts)]


def rel(path):
    return path.relative_to(ROOT).as_posix()


def strip_code(text):
    """Quita bloques y fragmentos de código, donde un texto con forma de enlace no es un enlace."""
    return INLINE_CODE.sub("", FENCE.sub("", text))


def slug(heading):
    """Ancla de un título con las reglas de GitHub: minúsculas, sin puntuación, espacios -> guiones."""
    text = re.sub(r"[`*_~]|\[([^\]]*)\]\([^)]*\)", r"\1", heading).strip().lower()
    text = re.sub(r"[^\w\- ]", "", text)
    return text.replace(" ", "-")


def anchors(path, cache={}):
    if path not in cache:
        found, seen = set(), {}
        for heading in HEADING.findall(FENCE.sub("", path.read_text(encoding="utf-8"))):
            base = slug(heading)
            count = seen.get(base, 0)
            found.add(base if count == 0 else f"{base}-{count}")
            seen[base] = count + 1
        cache[path] = found
    return cache[path]


def check_links(markdown):
    """Comprueba destinos y anclas; devuelve (enlaces comprobados, destinos alcanzados)."""
    checked, targets = 0, set()
    for path in markdown:
        for link in LINK.findall(strip_code(path.read_text(encoding="utf-8"))):
            if EXTERNAL.match(link):
                continue
            checked += 1
            target_part, _, anchor = link.partition("#")
            target = (path.parent / target_part).resolve() if target_part else path
            if not target.exists():
                errors.append(f"{rel(path)}: el destino no existe: {link}")
                continue
            targets.add(target)
            if anchor and target.suffix == ".md" and anchor not in anchors(target):
                errors.append(f"{rel(path)}: el ancla no existe en {rel(target)}: #{anchor}")
    return checked, targets


def check_orphans(files, targets):
    """Todo archivo versionado (salvo código y puntos de entrada) debe ser destino de algún enlace,
    directo o a través de una carpeta enlazada."""
    linked_dirs = [t for t in targets if t.is_dir()]
    orphans = 0
    for path in files:
        name = rel(path)
        if name in ENTRY_POINTS or name.startswith(IGNORED_PREFIXES):
            continue
        if path.resolve() in targets or any(d in path.resolve().parents for d in linked_dirs):
            continue
        orphans += 1
        errors.append(f"{name}: ningún documento lo enlaza")
    return orphans


def main():
    files = tracked_files()
    markdown = [p for p in files if p.suffix == ".md"]
    checked, targets = check_links(markdown)
    orphans = check_orphans(files, targets)
    info.append(f"Sin comprobar como huérfanos: {', '.join(sorted(ENTRY_POINTS))} y {', '.join(IGNORED_PREFIXES)}")

    print(f"check_links: {len(markdown)} archivos Markdown, {len(files)} archivos versionados")
    for message in errors:
        print(f"  [ERROR] {message}")
    for message in warnings:
        print(f"  [WARN]  {message}")
    for message in info:
        print(f"  [INFO]  {message}")
    status = "OK" if not errors else "FALLO"
    print(f"Resultado: {status} ({checked} enlaces comprobados, {len(targets)} destinos, {orphans} huérfanos; "
          f"{len(errors)} errores, {len(warnings)} advertencias)")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
