# ADR-0005: Base de datos H2 en memoria con Spring Data JPA

- **Estado:** Aceptado
- **Fecha:** 2026-10-18
- **Specs relacionadas:** `architecture/architecture.md`

## Contexto

Proyecto de aprendizaje que debe ejecutarse sin instalar infraestructura adicional. Se necesita una base de datos real para ejercitar el adaptador de persistencia y las restricciones de unicidad (DNI, email).

## Decisión

Usaremos H2 en memoria con Spring Data JPA dentro de `adapter.out.persistence`. La consola H2 estará habilitada en `/h2-console` solo para desarrollo. Para producción, se puede cambiar a PostgreSQL con un ADR que reemplace este.

## Alternativas consideradas

| Alternativa | A favor | En contra |
| :--- | :--- | :--- |
| PostgreSQL | Realista para producción. | Requiere instalación o Docker. |
| Persistencia en memoria (Map) | Cero dependencias. | No ejercita un adaptador real ni la restricción de unicidad del DNI. |
| H2 en archivo | Persistencia entre reinicios. | Más lento; no aporta valor en un proyecto de aprendizaje. |

## Consecuencias

- Los datos se pierden al reiniciar (aceptable en este contexto).
- Cambiar a PostgreSQL solo afecta `adapter.out.persistence` y la configuración, gracias a ADR-0001.
- La consola H2 permite inspeccionar los datos durante el desarrollo, en coherencia con el objetivo de ejecutar sin infraestructura.