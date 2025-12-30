# 2. Historias de usuario (especificación)

Convención:
- **Prioridad:** Alta / Media / Baja
- **Criterios de aceptación:** estilo Gherkin (Dado/Cuando/Entonces)
- **DoD (Definition of Done):** condiciones mínimas para considerar “terminada” una Historia de Usuario

---

## HU-01 — Registro e inicio de sesión
**Como** usuario  
**Quiero** registrarme e iniciar sesión  
**Para** acceder a mis datos personales de gastos de forma segura.

**Prioridad:** Alta

**Criterios de aceptación**
- Dado que no tengo cuenta, cuando introduzco usuario y contraseña válidos y pulso “Registrarse”, entonces el sistema crea el usuario y puedo iniciar sesión con esas creedenciales.
- Una vez creada la cuenta, introduzco las credenciales correctas, entonces accedo a la aplicación.
- Cuando las credenciales son incorrectas, entonces se muestra un mensaje de error y no se inicia sesión.

**DoD**
- Usuarios persistidos en `data/usuarios.json`.
- La sesión activa queda gestionada por el controlador (usuario actual).

---

## HU-02 — Registrar gasto personal
**Como** usuario  
**Quiero** registrar un gasto con cantidad, fecha, categoría y nota (opcional)
**Para** llevar control de mis gastos personales.

**Prioridad:** Alta

**Criterios de aceptación**
- Dado que estoy autenticado, cuando registro un gasto con cantidad > 0, fecha y categoría, entonces el gasto se guarda y aparece en el listado de gastos.
- Si falta un campo obligatorio, entonces el sistema no guarda y muestra el motivo.

**DoD**
- Persistencia en `data/gastos.json`.
- El gasto queda ligado al `usuarioId`.

---

## HU-03 — Editar y borrar gasto
**Como** usuario  
**Quiero** editar o borrar gastos  
**Para** corregir errores o mantener el historial actualizado.

**Prioridad:** Alta

**Criterios de aceptación**
- Cuando edito un gasto existente y guardo, entonces los cambios se guardan.
- Cuando borro un gasto, entonces desaparece del listado de gastos y de la persistencia.

**DoD**
- Operaciones de actualización y borrado a través del servicio/repositorio.

---

## HU-04 — Gestionar categorías
**Como** usuario  
**Quiero** usar categorías predefinidas y/o crear nuevas categorías  
**Para** clasificar mis gastos.

**Prioridad:** Alta

**Criterios de aceptación**
- Puedo crear una categoría indicando nombre y color (opcional).
- Las categorías se muestran en los formularios de gastos.

**DoD**
- Persistencia en `data/categorias.json`.

---

## HU-05 — Consultar gastos en tabla/lista
**Como** usuario  
**Quiero** ver mis gastos en formato tabla/lista  
**Para** consultar rápidamente el detalle.

**Prioridad:** Alta

**Criterios de aceptación**
- Se muestra una lista con cantidad, fecha, categoría y nota.
- Solo se muestran gastos del usuario autenticado.

---

## HU-06 — Filtrar gastos (mes, intervalo, categoría)
**Como** usuario  
**Quiero** filtrar mis gastos por meses, por intervalo de fechas y/o por categoría  
**Para** analizar periodos concretos o tipos de gasto.

**Prioridad:** Alta

**Criterios de aceptación**
- Puedo filtrar por uno o varios meses.
- Puedo filtrar por intervalo de fechas personalizado.
- Puedo filtrar por una o varias categorías.
- Puedo combinar filtros (por ejemplo: categoría + meses).

---

## HU-07 — Gráficas por categorías
**Como** usuario  
**Quiero** ver gráficas (barras y/o circular) por categorías  
**Para** entender la distribución de mis gastos.

**Prioridad:** Media

**Criterios de aceptación**
- La gráfica refleja los datos filtrados (si hay filtros activos).
- Se muestran totales por categoría.

---

## HU-08 — Visualización de gastos en calendario
**Como** usuario  
**Quiero** ver mis gastos en un calendario  
**Para** identificar patrones diarios o picos de gastos.

**Prioridad:** Media

**Criterios de aceptación**
- Puedo navegar por el calendario.
- Los gastos aparecen como entradas con su información esencial.

---

## HU-09 — Crear cuenta de gasto compartida (reparto equitativo)
**Como** usuario  
**Quiero** crear una cuenta compartida con varias personas  
**Para** repartir gastos de forma equitativa.

**Prioridad:** Alta

**Criterios de aceptación**
- Al crear la cuenta con N participantes, el porcentaje queda repartido equitativamente.
- Una vez creada la cuenta, la lista de participantes no se puede modificar.

---

## HU-10 — Cuenta compartida con reparto por porcentajes
**Como** usuario  
**Quiero** definir porcentajes de reparto por participante  
**Para** adaptar la cuenta con contribuciones diferentes.

**Prioridad:** Media

**Criterios de aceptación**
- La suma de porcentajes debe ser 100%.
- Si la suma no es 100%, el sistema lo indica y no permite guardar.

---

## HU-11 — Registrar gasto en cuenta compartida y calcular saldos
**Como** usuario  
**Quiero** registrar un gasto en una cuenta compartida indicando quién lo pagó  
**Para** actualizar automáticamente los saldos de cada participante.

**Prioridad:** Alta

**Criterios de aceptación**
- Al introducir un gasto pagado por una persona, el saldo del pagador aumenta y el del resto disminuye según el reparto.
- Los saldos se muestran por participante.

---

## HU-12 — Alertas configurables y notificaciones
**Como** usuario  
**Quiero** configurar alertas semanales/mensuales (y opcionalmente por categoría)  
**Para** recibir avisos al superar límites de gasto.

**Prioridad:** Alta

**Criterios de aceptación**
- Puedo definir un límite y periodicidad (semanal/mensual).
- Opcionalmente, puedo restringir a una categoría.
- Cuando se supera el límite establecido, se genera una notificación.

**DoD**
- Se guarda un historial de notificaciones consultable.

---

## HU-13 — Historial de notificaciones
**Como** usuario  
**Quiero** consultar notificaciones pasadas  
**Para** revisar alertas anteriores.

**Prioridad:** Media

**Criterios de aceptación**
- Puedo ver una lista de notificaciones con fecha y mensaje.

---

## HU-14 — Importar gastos desde fichero (preparado para distintos formatos)
**Como** usuario  
**Quiero** importar gastos desde un fichero externo  
**Para** incorporar datos sin introducirlos manualmente.

**Prioridad:** Media

**Criterios de aceptación**
- Puedo seleccionar un fichero de importación.
- El sistema transforma los datos al modelo `Gasto` y los persiste.
- El diseño permite añadir nuevos importadores sin tocar la lógica de la app.

---

## HU-15 — Uso por línea de comandos (CLI)
**Como** usuario  
**Quiero** gestionar gastos desde una CLI básica  
**Para** operar sin interfaz gráfica si lo necesito.

**Prioridad:** Baja

**Criterios de aceptación**
- Puedo listar/crear/borrar gastos desde consola.

