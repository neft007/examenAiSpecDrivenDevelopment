# Prompt 03 - Generar o actualizar la documentación derivada

## Cuándo usarlo

Para crear o actualizar la documentación de `docs/` a partir de las specs y el código, por ejemplo
al cerrar una historia o después de corregir specs. La documentación derivada resume; nunca sustituye
a `specs/`.

## Variables

| Variable | Qué poner | Ejemplo |
| :--- | :--- | :--- |
| `{{SECCION}}` | Documento o carpeta de `docs/` que se genera o actualiza. | `docs/tecnica/operaciones/reservarPlaza.md`, `docs/sync-report.md` |

## Prompt

```text
Genera (o actualiza) {{SECCION}} a partir de las specs y el código.

1. Lee las fuentes de {{SECCION}} antes de escribir:
   - Las specs que correspondan en specs/ (visión, RF, historias, dominio, arquitectura, ADR,
     contrato, trazabilidad).
   - El código y las pruebas de src/main/java y src/test/java que lo implementan.
   - Si existe, el resultado de ./mvnw clean test (número de pruebas por clase en
     target/surefire-reports) para las cifras de cobertura.

2. Escribe en castellano (specs/conventions.md):
   - Cabecera "Fuentes:" con enlaces relativos a los archivos de specs/ y las clases de las que se deriva.
   - Resumen conciso: tablas y listas, no una copia literal de la spec.
   - Enlaces a la sección concreta de la spec cuando la haya.
   - Cada dato (nombres de clases, pruebas, códigos, cifras) comprobado contra el código, no supuesto.

3. Mantén la coherencia:
   - Si encuentras una diferencia entre spec y código, NO la corrijas en la documentación ni en la
     spec: anótala en docs/sync-report.md (ID, tipo, dónde, diferencia y acción sugerida) y avísame.
   - Actualiza el índice (docs/tecnica/README.md) y los enlaces entre documentos si añades o
     renombras archivos.
   - Comprueba que todos los enlaces relativos existen.

4. No hace falta ejecutar mvn test si solo cambia Markdown. Propón el commit con prefijo docs:
   y no lo hagas hasta que lo confirme.
```
