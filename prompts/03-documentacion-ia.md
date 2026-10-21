# Prompt 03 - Generar o actualizar la documentación derivada

## Cuándo usarlo

Para crear o actualizar la documentación de `docs/` a partir de las specs y el código, por ejemplo
al cerrar una historia, después de corregir specs o al preparar la entrega (`docs/entrega/`). La
documentación derivada resume; nunca sustituye a `specs/`.

## Variables

| Variable | Qué poner | Ejemplo |
| :--- | :--- | :--- |
| `{{SECCION}}` | Documento o carpeta de `docs/` que se genera o actualiza. | `docs/tecnica/operaciones/reservarPlaza.md`, `docs/sync-report.md`, `docs/entrega/` |

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
   - Si cambia el flujo de un servicio (puertos llamados, orden de las comprobaciones o excepciones),
     actualiza también su diagrama en docs/tecnica/diagramas.md.
   - Comprueba que todos los enlaces relativos existen.
   - Mantén actualizado specs/CHANGELOG.md: si desde su última entrada hay commits que cambian
     specs/ (git log -- specs/), añade una entrada con fecha, descripción, motivo, archivos de spec
     modificados, compatibilidad del contrato y commits.

4. Si {{SECCION}} es la documentación de entrega (docs/entrega/), también se genera a partir de las
   specs y el código, y debe incluir:
   - README.md: estado del proyecto, cómo ejecutarlo, verificaciones realizadas y enlaces a la
     documentación técnica y a la matriz. Puede citar specs/CHANGELOG.md para resumir cómo
     evolucionaron las specs.
   - matriz-trazabilidad.md: RF -> US -> CA -> INV -> operación -> pruebas concretas por capa,
     basada en specs/traceability.md y en los métodos de prueba reales de src/test/java.
   - evidencia/: la salida real de tools/spec_lint.py y tools/spec_sync.py, y un resumen de
     target/surefire-reports por capa y por clase.
   Vuelve a ejecutar ./mvnw clean test y los scripts justo antes de generarla, extrae las cifras y
   los nombres de las pruebas con un script (no a mano) e indica el commit sobre el que se generó.
   La revisión final con criterio (docs/revisiones/2026-10-20-revision-final.md) recoge las
   verificaciones, los hallazgos y el estado final de la V1; enlázala desde el README de la entrega.

5. No hace falta ejecutar mvn test si solo cambia Markdown. Propón el commit con prefijo docs:
   y no lo hagas hasta que lo confirme.
```
