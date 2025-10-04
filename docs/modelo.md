
```mermaid
classDiagram
  direction TB

  %% --- Núcleo de dominio ---
  class Usuario {
    +id: UUID
    +nombre: String
    +email: String
  }

  class Gasto {
    +id: UUID
    +monto: BigDecimal
    +fecha: LocalDateTime
    +descripcion: String
  }

  class Categoria {
    +id: UUID
    +nombre: String
  }

  Usuario "1" --> "0..*" Gasto : registra
  Categoria "1" <-- "0..*" Gasto : clasifica

  %% --- Filtros/Consultas ---
  class FiltroGastos {
    <<value object>>
    +meses: Set<Month>
    +rangoFechas: Range<LocalDate>
    +categorias: Set<Categoria>
  }
  Gasto ..> FiltroGastos : se filtra por

  %% --- Alertas (Strategy) ---
  class Alerta {
    <<interface>>
    +umbral: BigDecimal
    +esDisparada(coleccion:Gastos, periodo): boolean
    +getDescripcion(): String
  }
  class AlertaSemanal {
    +categoria: Optional<Categoria>
  }
  class AlertaMensual {
    +categoria: Optional<Categoria>
  }
  Alerta <|.. AlertaSemanal
  Alerta <|.. AlertaMensual
  Usuario "1" --> "0..*" Alerta : configura

  class Notificacion {
    +id: UUID
    +fecha: LocalDateTime
    +mensaje: String
    +leida: boolean
  }
  Usuario "1" --> "0..*" Notificacion : recibe

  %% --- Cuentas compartidas ---
  class CuentaCompartida {
    +id: UUID
    +nombre: String
    +equidad: boolean
  }
  class Miembro {
    +id: UUID
    +nombre: String
    +porcentaje: BigDecimal
    +saldo: BigDecimal
  }
  class GastoCompartido {
    +id: UUID
    +monto: BigDecimal
    +fecha: LocalDateTime
    +descripcion: String
    +pagadoPor: Miembro
  }

  CuentaCompartida "1" --> "2..*" Miembro : fija al crear
  CuentaCompartida "1" --> "0..*" GastoCompartido : contiene
  GastoCompartido "1" --> "1" Miembro : pagadoPor
  GastoCompartido  --|> Gasto

