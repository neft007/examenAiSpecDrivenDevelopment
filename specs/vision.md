# Visión - Sistema de Reservas para Gimnasio

## 1. Propósito
Gestionar de forma centralizada las reservas de clases en un gimnasio, asegurando que los socios
solo puedan reservar en clases con aforo disponible y que sus membresías estén vigentes.

## 2. Problema a resolver
- Los socios reservan clases sin verificar si su membresía está activa.
- Se producen sobre-reservas porque no hay control de aforo en tiempo real.
- Los socios se presentan a clases sin haber reservado, ocupando plazas de otros.
- No hay un historial claro de las reservas de cada socio.

## 3. Alcance
- Registrar socios con sus datos y estado de membresía.
- Crear clases con horario, instructor y aforo máximo.
- Permitir que un socio reserve una plaza en una clase.
- Permitir que un socio cancele una reserva.
- Consultar las reservas de un socio y las plazas disponibles de una clase.

## 4. Fuera de alcance (este release)
- Pagos en línea o gestión de cobros.
- Notificaciones por email o SMS.
- Gestión de instructores (solo se registra su nombre).
- Listas de espera para clases llenas.
- Historial de asistencias (solo reservas).
- Cambio de membresía (solo se asigna al registrar el socio).

## 5. Partes interesadas
| Rol | Interés principal |
| :--- | :--- |
| Socio | Reservar y cancelar clases de forma sencilla y sin errores. |
| Recepcionista | Verificar la vigencia de membresías y el aforo de las clases. |
| Administrador | Crear clases y consultar la ocupación. |