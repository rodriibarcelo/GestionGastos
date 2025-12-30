# 1. Modelo de dominio (diagrama de clases)

Este documento recoge el **modelo de dominio principal** del proyecto.

> **Nota de persistencia:** en la implementación, varias asociaciones se guardan por **identificador (UUID)** (por ejemplo, `Gasto.categoriaId`) para simplificar la serialización en JSON. La relación se resuelve en servicios/repositorios.

```mermaid
classDiagram
  direction TB

  class Categoria {
    +UUID id
    +String nombre
    +String colorHex
  }

  class Usuario {
    +UUID id
    +String nombreUsuario
    +String password
  }

  class Persona {
    +UUID id
    +String nombre
  }

  class Participacion {
    +UUID usuarioId
    +UUID cuentaId
    +double porcentaje
    +BigDecimal saldo
  }

  class CuentaCompartida {
    +UUID id
    +String nombre
    +List~Participacion~ participantes
  }

  class Gasto {
    +UUID id
    +BigDecimal cantidad
    +LocalDate fecha
    +UUID categoriaId
    +String nota
    +UUID cuentaCompartidaId
    +UUID personaId
    +UUID usuarioId
  }

  class Notificacion {
    +UUID id
    +UUID usuarioId
    +String mensaje
    +LocalDateTime fecha
  }

  %% --- Asociaciones conceptuales ---
  Gasto "0..*" --> "1" Categoria : categoriaId
  Gasto "0..*" --> "0..1" CuentaCompartida : cuentaCompartidaId
  Gasto "0..*" --> "0..1" Persona : personaId
  Gasto "0..*" --> "1" Usuario : usuarioId

  CuentaCompartida "1" --> "0..*" Participacion : participantes
  Participacion "0..*" --> "1" Usuario : usuarioId
  Participacion "0..*" --> "1" CuentaCompartida : cuentaId

  Usuario "1" --> "0..*" Notificacion : historial (usuarioId)
```

## Reglas y observaciones relevantes

- **IDs y referencias:** las entidades usan `UUID` como identificador y las asociaciones se expresan mediante IDs (`categoriaId`, `cuentaCompartidaId`, etc.) para persistir en JSON.
- **Cuenta compartida:**
  - La cuenta agrupa participantes (`Participacion`) y mantiene un **saldo** por participante.
  - Cada `Participacion` tiene un **porcentaje** (0–100). En modo equitativo, el porcentaje se reparte por igual.
  - Restricción de negocio del enunciado: **una vez creada** la cuenta con la lista de participantes, **no se modifica** dicha lista.
- **Gasto:**
  - Un gasto siempre pertenece a un `Usuario`.
  - Opcionalmente puede asociarse a una `CuentaCompartida` y/o a una `Persona` (pagador en la cuenta).
- **Notificaciones:** se almacenan como historial ligado al `Usuario`.

