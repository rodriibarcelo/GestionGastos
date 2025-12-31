# 3. Diagrama de interacción (secuencia)

Se muestra el flujo de la historia **HU-02 — Registrar gasto personal**, incluyendo:
- Persistencia del gasto
- Evaluación de alertas
- Persistencia de notificaciones (historial)

```mermaid
sequenceDiagram
  autonumber
  actor U as Usuario
  participant V as Vista (JavaFX)
  participant C as Controlador
  participant SG as ServicioGastos
  participant GR as GastoRepositoryJson
  participant GA as GestorAlertas
  participant NR as NotificacionRepositoryJson
  participant JS as JsonStorage

  U->>V: Pulsa "Guardar gasto" (formulario completo)
  V->>C: registrarGasto(cantidad, fecha, categoriaId, nota)

  C->>SG: registrar(gasto)
  SG->>GR: save(gasto)
  GR->>JS: write(gastos.json)
  JS-->>GR: OK
  GR-->>SG: OK
  SG-->>C: OK

  C->>SG: listarPorUsuario(usuarioId)
  SG->>GR: findByUsuario(usuarioId)
  GR-->>SG: List<Gasto>
  SG-->>C: List<Gasto>

  C->>GA: evaluar(gasto, historico)
  GA-->>C: List<String> mensajes

  opt Si hay alertas (mensajes no vacío)
    loop Por cada mensaje
      C->>NR: save(notificacion)
      NR->>JS: write(notificaciones.json)
      JS-->>NR: OK
    end
  end

  C-->>V: OK + (mensajes si hay)
  V-->>U: Muestra confirmación/alertas


```
Por claridad, se omiten en el diagrama los flujos alternativos de validación y errores de persistencia, que se gestionan mediante mensajes de error en la interfaz.
