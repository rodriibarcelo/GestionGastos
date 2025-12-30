# 1. Diagrama de clases del dominio

Este documento recoge el **modelo de dominio** principal del proyecto.

> Nota: en la implementación, varias asociaciones se persisten **por identificador (UUID)** (por ejemplo, `Gasto.categoriaId`), para simplificar la serialización JSON.

```mermaid
classDiagram

class Usuario {
  UUID id
  String nombreUsuario
  String password
}

class Categoria {
  UUID id
  String nombre
  String colorHex
}

class Gasto {
  UUID id
  BigDecimal cantidad
  LocalDate fecha
  UUID categoriaId
  String nota
  UUID cuentaCompartidaId
  UUID personaId
  UUID usuarioId
}

class CuentaCompartida {
  UUID id
  String nombre
  List~Participacion~ participantes
}

class Participacion {
  UUID usuarioId
  UUID cuentaId
  double porcentaje
  BigDecimal saldo
}

class Notificacion {
  UUID id
  UUID usuarioId
  String mensaje
  LocalDateTime fecha
}

class Persona {
  UUID id
  String nombre
}

Usuario "1" --> "0..*" Gasto : registra
Categoria "1" <-- "0..*" Gasto : categoriaId
CuentaCompartida "1" <-- "0..*" Gasto : cuentaCompartidaId 
Persona "1" <-- "0..*" Gasto : personaId 

CuentaCompartida "1" --> "1..*" Participacion : participantes
Usuario "1" <-- "0..*" Participacion : usuarioId

Usuario "1" --> "0..*" Notificacion : historial
```

## Reglas y observaciones relevantes

- **Gasto**: representa un registro económico con **cantidad**, **fecha**, **categoría** y una nota opcional. Puede ser **personal** (sin cuenta compartida) o estar vinculado a una **CuentaCompartida**.
- **CuentaCompartida**: agrupa a varios usuarios participantes (vía `Participacion`) y mantiene el reparto por **porcentaje**.
- **Participacion**: guarda el **porcentaje** del reparto y el **saldo** acumulado (positivo si el grupo le debe, negativo si debe al grupo). El saldo se actualiza al registrar un gasto compartido.
- **Notificacion**: representa los avisos generados por el sistema de alertas y se persiste como historial del usuario.
- **Persona**: entidad disponible para representar a pagadores dentro de una cuenta (en el estado actual del proyecto, la UI/flujo principal se apoya en `Usuario`).
