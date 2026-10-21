# registrarSocio

> **Fuentes:** [RF-01](../../../specs/requirements/functional.md#rf-01-registrar-socio),
> [US01](../../../specs/user-stories/US01-registrar-socio.md),
> [contrato](../../../specs/api/gimnasio-openapi.yaml) (`/api/v1/socios`),
> [`traceability.md`](../../../specs/traceability.md) y el código de `SocioController`,
> `RegistrarSocioService`, `Socio`, `Dni`, `Email` y `SocioPersistenceAdapter`.

`POST /api/v1/socios` · tag `Socios` · actor: Recepcionista

## Entrada y salida

- **Request** `RegistrarSocioRequest`: `dni`, `nombre`, `email` (todos obligatorios).
- **201** `SocioResponse` con cabecera `Location: /api/v1/socios/{id}`. El socio se crea con membresía
  `ACTIVA` y `fechaAlta` = instante del `Clock`.
- `RegistrarSocioRequest` y `SocioResponse` se generan a partir del contrato, y `SocioController` implementa
  la interfaz generada `SociosApi`. La configuración del generador está documentada en
  [`specs/resources/plugins.md`](../../../specs/resources/plugins.md).

## Flujo

```text
SocioController → SocioWebMapper.toCommand → RegistrarSocioUseCase.registrar
  RegistrarSocioService:
    new Dni(dni)            INV-01
    new Email(email)        INV-04
    existsByDni             INV-02
    existsByEmail           INV-03
    Socio.registrar(...)    INV-05, membresía ACTIVA
    save                    restricciones únicas de BD como respaldo
  → SocioResult → SocioWebMapper.toResponse → 201
```

El diagrama de secuencia de esta operación está en [diagramas.md](../diagramas.md#registrarsocio-us01).

## Reglas y errores

| Regla | Invariante | Respuesta | CA |
| :--- | :--- | :--- | :--- |
| DNI de 8 dígitos | INV-01 | 400 `VALIDATION_ERROR` | CA-03 |
| DNI único | INV-02 | 409 `DNI_DUPLICADO` | CA-02 |
| Email único | INV-03 | 409 `EMAIL_DUPLICADO` | CA-04 |
| Email con formato válido | INV-04 | 400 `VALIDATION_ERROR` | CA-05 |
| Nombre: 3–100, sin espacios en los extremos | INV-05 | 400 `VALIDATION_ERROR` | CA-06 |

Si dos registros simultáneos pasan las comprobaciones previas, la restricción única (`uk_socios_dni`,
`uk_socios_email`) los frena y el adaptador lanza la misma excepción de duplicado.

## Pruebas

| Capa | Clase | CA cubiertos |
| :--- | :--- | :--- |
| Dominio | `US01RegistrarSocioTest` | CA-01, CA-03, CA-05, CA-06 |
| Aplicación | `RegistrarSocioServiceTest` | CA-01, CA-02, CA-04 |
| Persistencia | `SocioPersistenceAdapterTest` | CA-01, CA-02, CA-04 (restricciones de BD) |
| Web | `SocioControllerRegisterTest` | CA-01 a CA-06 |
