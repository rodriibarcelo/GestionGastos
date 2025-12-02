package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.CuentaCompartida;
import gestiongastos.dominio.Gasto;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class VistaMain {

    private final BorderPane root;
    private final Controlador ctrl;

    // Alta de gasto
    private final TextField tfCantidad;
    private final DatePicker dpFecha;
    private final ComboBox<Categoria> cbCategoria;
    private final ComboBox<Object> cbCuenta;
    private final TextField tfNota;
    private final Button btnAdd;
    private final Button btnFiltrar;
    private final Button btnEdit;
    private final Button btnDelete;

    // Tabla
    private final TableView<GastoRow> table;
    private final ObservableList<GastoRow> rows;

    // Estado inferior
    private final Label lblTotal;
    private final Label lblError;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat MONEY;

    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols(new Locale("es", "ES"));
        s.setDecimalSeparator(',');
        s.setGroupingSeparator('.');
        MONEY = new DecimalFormat("#,##0.00 €");
        MONEY.setDecimalFormatSymbols(s);
    }

    public VistaMain() {
        this.ctrl = Controlador.getInstance();
        this.root = new BorderPane();
        this.root.setPadding(new Insets(12));

        // ============================================================
        // CABECERA
        // ============================================================
        MenuBar bar = new MenuBar();

        Menu menuDatos = new Menu("Datos");
        MenuItem miCategorias = new MenuItem("Categorías…");
        MenuItem miImportar = new MenuItem("Importar gastos…");
        MenuItem miAlertas = new MenuItem("Configurar alertas…");
        MenuItem miCuentas = new MenuItem("Cuentas compartidas…");

        menuDatos.getItems().addAll(miCategorias, miImportar, miAlertas, miCuentas);

        Menu menuVer = new Menu("Ver");
        MenuItem miEstadisticas = new MenuItem("Estadísticas…");
        MenuItem miCalendario = new MenuItem("Calendario de gastos…");
        menuVer.getItems().addAll(miEstadisticas, miCalendario);

        bar.getMenus().addAll(menuDatos, menuVer);

        Label title = new Label("Gestión de Gastos");
        title.setFont(Font.font(20));
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(6, 0, 6, 0));

        VBox header = new VBox(bar, titleBox);
        root.setTop(header);

        // ============================================================
        // FORMULARIO SUPERIOR
        // ============================================================
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(8));
        form.setAlignment(Pos.CENTER);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHalignment(HPos.RIGHT);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setHalignment(HPos.RIGHT);
        ColumnConstraints c4 = new ColumnConstraints();
        c4.setHgrow(Priority.ALWAYS);
        ColumnConstraints c5 = new ColumnConstraints();
        form.getColumnConstraints().addAll(c1, c2, c3, c4, c5);

        tfCantidad = new TextField();
        tfCantidad.setPromptText("Ej. 12,50");
        tfCantidad.setAlignment(Pos.CENTER_RIGHT);
        aplicarFormatterDecimal(tfCantidad);

        dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d == null ? "" : DF.format(d);
            }

            @Override
            public LocalDate fromString(String s) {
                return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DF);
            }
        });

        cbCategoria = new ComboBox<>(FXCollections.observableArrayList(ctrl.listarCategorias()));
        cbCategoria.setMaxWidth(Double.MAX_VALUE);

        // Combo de cuentas
        cbCuenta = new ComboBox<>();
        cbCuenta.setConverter(new StringConverter<>() {
            @Override
            public String toString(Object value) {
                if (value == null) return "";
                if (value instanceof CuentaCompartida c) return c.getNombre();
                return value.toString();   // "Personal"
            }

            @Override
            public Object fromString(String s) {
                return s; // no lo usamos
            }
        });

        // cargar opciones ("Personal" + cuentas compartidas)
        actualizarComboCuentas();

        tfNota = new TextField();
        tfNota.setPromptText("Descripción opcional…");

        btnAdd     = new Button("Añadir");
        btnFiltrar = new Button("Filtrar…");
        btnEdit    = new Button("Editar");
        btnDelete  = new Button("Eliminar");

        int r = 0;
        form.add(new Label("Cantidad (€):"), 0, r);
        form.add(tfCantidad, 1, r);
        form.add(new Label("Fecha:"), 2, r);
        form.add(dpFecha, 3, r);

        HBox acciones = new HBox(8, btnFiltrar, btnAdd, btnEdit, btnDelete);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        form.add(acciones, 4, r);

        r++;
        form.add(new Label("Cuenta:"), 0, r);
        form.add(cbCuenta, 1, r);

        r++;
        form.add(new Label("Categoría:"), 0, r);
        form.add(cbCategoria, 1, r);
        form.add(new Label("Nota:"), 2, r);
        form.add(tfNota, 3, r);

        // ============================================================
        // TABLA
        // ============================================================
        table = new TableView<>();
        rows = FXCollections.observableArrayList();
        table.setItems(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);


        TableColumn<GastoRow, String> cCuenta = new TableColumn<>("Cuenta");
        cCuenta.setCellValueFactory(new PropertyValueFactory<>("cuenta"));
        cCuenta.setMinWidth(140);
        
        TableColumn<GastoRow, LocalDate> cFecha = new TableColumn<>("Fecha");
        cFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cFecha.setMinWidth(110);
        cFecha.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DF.format(item));
            }
        });



        TableColumn<GastoRow, String> cCat = new TableColumn<>("Categoría");
        cCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        cCat.setMinWidth(160);

        TableColumn<GastoRow, BigDecimal> cCant = new TableColumn<>("Cantidad");
        cCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cCant.setMinWidth(120);
        cCant.setStyle("-fx-alignment: CENTER-RIGHT;");
        cCant.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : MONEY.format(item));
            }
        });

        TableColumn<GastoRow, String> cNota = new TableColumn<>("Nota");
        cNota.setCellValueFactory(new PropertyValueFactory<>("nota"));
        cNota.setMinWidth(180);

        // añadimos la columna Cuenta en la tabla
        table.getColumns().setAll(cCuenta,cFecha, cCat, cCant, cNota);

        // Menú contextual
        MenuItem miEditCtx = new MenuItem("Editar");
        MenuItem miDeleteCtx = new MenuItem("Eliminar");
        miEditCtx.setOnAction(e -> onEdit());
        miDeleteCtx.setOnAction(e -> onDelete());
        table.setContextMenu(new ContextMenu(miEditCtx, miDeleteCtx));

        VBox center = new VBox(10, form, table);
        center.setPadding(new Insets(8, 8, 0, 8));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(center);

        // ============================================================
        // PIE
        // ============================================================
        lblError = new Label(" ");
        lblError.setStyle("-fx-text-fill: red;");

        lblTotal = new Label("Total: 0,00 €");
        lblTotal.setStyle("-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, lblError, spacer, lblTotal);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(10, 8, 10, 8));
        root.setBottom(bottom);

        // ============================================================
        // DATOS INICIALES
        // ============================================================
        cargarTabla(ctrl.listarGastos());

        // ============================================================
        // LISTENERS
        // ============================================================
        btnAdd.setOnAction(e -> onAdd());
        btnFiltrar.setOnAction(e -> onOpenFiltro());
        btnEdit.setOnAction(e -> onEdit());
        btnDelete.setOnAction(e -> onDelete());
        miCategorias.setOnAction(e -> onOpenCategorias());

        miEstadisticas.setOnAction(e -> {
            VistaEstadisticas est = new VistaEstadisticas();
            Utils.openDialog(root, "Estadísticas", est.getRoot(), 900, 640);
        });

        miCalendario.setOnAction(e -> {
            VistaCalendario vc = new VistaCalendario();
            Utils.openDialog(root, "Calendario de Gastos", vc.getRoot(), 900, 700);
        });

        miCuentas.setOnAction(e -> {
            VistaCuentasCompartidas vc = new VistaCuentasCompartidas();
            Utils.openDialog(root, "Cuentas compartidas", vc.getRoot(), 600, 500);
            actualizarComboCuentas(); // refresca el combo de la pantalla principal
        });

        btnEdit.disableProperty().bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));
        btnDelete.disableProperty().bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));
    }

    public Parent getRoot() {
        return root;
    }

    // ============================================================
    // ACCIONES
    // ============================================================
    private void onAdd() {
        lblError.setText(" ");

        try {
            BigDecimal cantidad = parseCantidad(tfCantidad.getText().trim());
            if (cantidad.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");

            LocalDate fecha = dpFecha.getValue();
            if (fecha == null)
                throw new IllegalArgumentException("Selecciona una fecha.");

            Categoria cat = cbCategoria.getValue();
            if (cat == null)
                throw new IllegalArgumentException("Selecciona una categoría.");

            String nota = tfNota.getText().trim();

            Object seleccionCuenta = cbCuenta.getValue();
            List<String> mensajes;

            if ("Personal".equals(seleccionCuenta)) {

                mensajes = ctrl.registrarGasto(
                        cantidad,
                        fecha,
                        cat.getId(),
                        nota
                );

            } else if (seleccionCuenta instanceof CuentaCompartida cuenta) {

                mensajes = ctrl.registrarGastoCompartido(
                        cantidad,
                        fecha,
                        cat.getId(),
                        cuenta.getId(),
                        nota
                );

            } else {
                throw new IllegalStateException("Cuenta seleccionada inválida.");
            }

            cargarTabla(ctrl.listarGastos());

            if (!mensajes.isEmpty()) {
                Utils.alertWarn(String.join("\n", mensajes));
            }

            tfCantidad.clear();
            tfNota.clear();
            tfCantidad.requestFocus();

        } catch (Exception ex) {
            lblError.setText("❌ " + ex.getMessage());
        }
    }

    private void onEdit() {
        GastoRow sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            return;
        }

        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Editar gasto");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        DatePicker dp = new DatePicker(sel.getFecha());
        dp.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(final LocalDate d) {
                return d == null ? "" : DF.format(d);
            }

            @Override
            public LocalDate fromString(final String s) {
                return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DF);
            }
        });

        TextField tfImp = new TextField(MONEY.format(sel.getCantidad()).replace(" €", ""));
        tfImp.setAlignment(Pos.CENTER_RIGHT);
        aplicarFormatterDecimal(tfImp);

        ComboBox<Categoria> cb = new ComboBox<>(FXCollections.observableArrayList(ctrl.listarCategorias()));
        cb.getSelectionModel().selectFirst();
        cb.getItems()
          .stream()
          .filter(c -> c.getNombre().equals(sel.getCategoria()))
          .findFirst()
          .ifPresent(c -> cb.getSelectionModel().select(c));

        TextField tfN = new TextField(sel.getNota());

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));
        gp.add(new Label("Fecha:"), 0, 0);
        gp.add(dp, 1, 0);
        gp.add(new Label("Cantidad (€):"), 0, 1);
        gp.add(tfImp, 1, 1);
        gp.add(new Label("Categoría:"), 0, 2);
        gp.add(cb, 1, 2);
        gp.add(new Label("Nota:"), 0, 3);
        gp.add(tfN, 1, 3);
        dlg.getDialogPane().setContent(gp);

        dlg.setResultConverter(bt -> bt);
        ButtonType res = dlg.showAndWait().orElse(ButtonType.CANCEL);
        if (res != ButtonType.OK) {
            return;
        }

        try {
            BigDecimal nuevaCantidad = parseCantidad(tfImp.getText().trim());
            LocalDate nuevaFecha = dp.getValue();
            Categoria nuevaCat = cb.getValue();
            String nuevaNota = tfN.getText().trim();

            if (nuevaFecha == null) {
                throw new IllegalArgumentException("Selecciona una fecha.");
            }
            if (nuevaCat == null) {
                throw new IllegalArgumentException("Selecciona una categoría.");
            }
            if (nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
            }

            ctrl.actualizarGasto(sel.getOriginal().getId(), nuevaCantidad, nuevaFecha, nuevaCat.getId(), nuevaNota);

            cargarTabla(ctrl.listarGastos());
        } catch (Exception ex) {
            Utils.alertError("No se pudo actualizar: " + ex.getMessage());
        }
    }

    private void onDelete() {
        GastoRow sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            return;
        }

        Alert a = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar el gasto de " + DF.format(sel.getFecha()) + " (" + MONEY.format(sel.getCantidad()) + ")?",
                ButtonType.YES,
                ButtonType.NO
        );
        a.setHeaderText(null);
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    ctrl.borrarGasto(sel.getOriginal().getId());
                    cargarTabla(ctrl.listarGastos());
                } catch (Exception ex) {
                    Utils.alertError("No se pudo eliminar: " + ex.getMessage());
                }
            }
        });
    }

    private void onOpenFiltro() {
        FiltroGastosDialog dlg = new FiltroGastosDialog();
        Utils.openDialog(root, "Filtros de gastos", dlg.getRoot(), 420, 260);

        if (dlg.isOk()) {
            var c = dlg.getCriterios();
            cargarTabla(ctrl.filtrarGastos(c.getDesde(), c.getHasta(), c.getCategoriaId()));
        }
    }

    private void onOpenCategorias() {
        VistaCategorias dlg = new VistaCategorias();
        Utils.openDialog(root, "Categorías", dlg.getRoot(), 520, 420);

        cbCategoria.getItems().setAll(ctrl.listarCategorias());
        cargarTabla(ctrl.listarGastos());
    }

    private void cargarTabla(List<Gasto> gastos) {
        rows.setAll(
                gastos.stream()
                        .map(g -> new GastoRow(
                                g,
                                g.getFecha(),
                                nombreCategoria(g.getCategoriaId()),
                                nombreCuenta(g),           // CUENTA
                                g.getCantidad(),
                                g.getNota()))
                        .toList()
        );

        BigDecimal total = gastos.stream()
                .map(Gasto::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotal.setText("Total: " + MONEY.format(total));
    }

    private String nombreCategoria(java.util.UUID id) {
        return ctrl.listarCategorias()
                .stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(Categoria::getNombre)
                .orElse("");
    }

    // Devuelve el nombre de la cuenta a mostrar en la tabla
    private String nombreCuenta(Gasto g) {
        // ⚠️ AJUSTA AQUÍ el getter según tu clase Gasto
        var cuentaId = g.getCuentaCompartidaId(); // por ejemplo: getCuentaCompartidaId()

        if (cuentaId == null) {
            return "Personal";
        }

        return ctrl.getServicioCuentas().listar()
                .stream()
                .filter(c -> c.getId().equals(cuentaId))
                .findFirst()
                .map(CuentaCompartida::getNombre)
                .orElse("Cuenta compartida");
    }

    private static void aplicarFormatterDecimal(TextField tf) {
        tf.setTextFormatter(new TextFormatter<>(change -> {
            String nxt = change.getControlNewText();
            return nxt.matches("\\d{0,10}([\\.,]\\d{0,2})?") ? change : null;
        }));
    }

    private static BigDecimal parseCantidad(String txt) {
        if (txt.isBlank())
            throw new IllegalArgumentException("Introduce una cantidad.");
        txt = txt.replace(',', '.');
        return new BigDecimal(txt);
    }

    private void actualizarComboCuentas() {
        cbCuenta.getItems().clear();

        // opción por defecto
        cbCuenta.getItems().add("Personal");

        // añadir todas las cuentas compartidas
        ctrl.getServicioCuentas().listar()
                .forEach(c -> cbCuenta.getItems().add(c));

        cbCuenta.getSelectionModel().selectFirst();
    }

    // ============================================================
    // DTO FILA TABLA
    // ============================================================

    public static class GastoRow {

        private final Gasto original;
        private final LocalDate fecha;
        private final String categoria;
        private final String cuenta;      // NUEVO
        private final BigDecimal cantidad;
        private final String nota;

        public GastoRow(Gasto original,
                        LocalDate fecha,
                        String categoria,
                        String cuenta,
                        BigDecimal cantidad,
                        String nota) {
            this.original = original;
            this.fecha = fecha;
            this.categoria = categoria;
            this.cuenta = cuenta;
            this.cantidad = cantidad;
            this.nota = nota;
        }

        public Gasto getOriginal() {
            return original;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public String getCategoria() {
            return categoria;
        }

        public String getCuenta() {
            return cuenta;
        }

        public BigDecimal getCantidad() {
            return cantidad;
        }

        public String getNota() {
            return nota;
        }
    }
}
