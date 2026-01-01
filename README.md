# GestionGastos

Aplicación de escritorio (JavaFX) para el **registro y análisis de gastos**, con persistencia en **JSON**, visualización en **gráficos** y **calendario**, cuentas compartidas, alertas e importación.

**Participantes:**

- **José Tomás Piñeda Rojas** — Grupo 3.1  
  josetomas.pinerar@um.es  
- **Rodrigo Barceló Arce** — Grupo 3.4  
  r.barceloarce@um.es  
- **Francisco Javier Tomás López** — Grupo 3.3  
  fj.tomaslopez@um.es




## Requisitos

- JDK 17
- Maven

## Ejecución (GUI)

```bash
mvn clean javafx:run
```

## Ejecución (CLI)

- Desde IDE: ejecutar `gestiongastos.cli.MainCLI`.

## Funcionalidades

- Registro / edición / borrado de gastos
- Gestión de categorías (nombre + color)
- Filtros por fechas y categoría
- Estadísticas (circular, barras, evolución)
- Vista calendario (CalendarFX)
- Cuentas compartidas con reparto por porcentaje
- Alertas configurables e historial de notificaciones
- Importación de gastos desde CSV

## Persistencia

Los datos se guardan en la carpeta `data/` en ficheros JSON.

## Documentación (memoria)

La memoria del proyecto está en [`docs/`](./docs/):

- `docs/modelo.md` — diagrama de clases del dominio
- `docs/historias_usuario.md` — historias de usuario
- `docs/diagrama_interaccion.md` — diagrama de interacción (una HU)
- `docs/arquitectura.md` — arquitectura y decisiones de diseño
- `docs/patrones.md` — patrones de diseño
- `docs/manual_usuario.md` — manual de usuario (con capturas)
