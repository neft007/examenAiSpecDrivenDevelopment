# crearClase

> **Fuentes:** [RF-02](../../../specs/requirements/functional.md#rf-02-crear-clase),
> [US02](../../../specs/user-stories/US02-crear-clase.md),
> [contrato](../../../specs/api/gimnasio-openapi.yaml) (`/api/v1/clases`),
> [`traceability.md`](../../../specs/traceability.md) y el código de `ClaseController`,
> `CrearClaseService`, `Clase` y `ClasePersistenceAdapter`.

`POST /api/v1/clases` · tag `Clases` · actor: Administrador

## Entrada y salida

- **Request** `CrearClaseRequest`: `nombre`, `instructor`, `fechaInicio`, `duracionMinutos`,
  `aforoMaximo` (todos obligatorios).
- **201** `ClaseResponse` con cabecera `Location: /api/v1/clases/{id}`. La clase se crea en `PROGRAMADA`.

## Flujo

```text
ClaseController → ClaseWebMapper.toCommand → CrearClaseUseCase.crear
  CrearClaseService:
    Clase.crear(..., clock)   INV-16, INV-17, INV-06, INV-08, INV-07 → PROGRAMADA
    save
  → ClaseResult → ClaseWebMapper.toResponse → 201
```

Todas las reglas viven en `Clase.crear()`; el servicio solo orquesta.

## Reglas y errores

| Regla | Invariante | Respuesta | CA |
| :--- | :--- | :--- | :--- |
| `fechaInicio` futura (igual a "ahora" se rechaza) | INV-06 | 400 `VALIDATION_ERROR` | CA-02 |
| Aforo 1–50 | INV-07 | 400 `VALIDATION_ERROR` | CA-03 |
| Duración 1–180 min | INV-08 | 400 `VALIDATION_ERROR` | CA-04 |
| Nombre: 3–100, sin espacios en los extremos | INV-16 | 400 `VALIDATION_ERROR` | CA-05 |
| Instructor: 3–100, sin espacios en los extremos | INV-17 | 400 `VALIDATION_ERROR` | CA-06 |

El contrato no declara 409 para esta operación: crear una clase no tiene conflictos de unicidad.

## Pruebas

| Capa | Clase | CA cubiertos |
| :--- | :--- | :--- |
| Dominio | `US02CrearClaseTest` | CA-01 a CA-06, con valores límite |
| Aplicación | `CrearClaseServiceTest` | CA-01 a CA-06 (y que nada se guarda si se rechaza) |
| Persistencia | `ClasePersistenceAdapterTest` | CA-01 (guardar y restaurar) |
| Web | `ClaseControllerCreateTest` | CA-01 a CA-06 |
