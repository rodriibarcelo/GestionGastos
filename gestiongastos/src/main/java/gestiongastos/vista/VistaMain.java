package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VistaMain {

    private final BorderPane root;
    private final Controlador ctrl;

    // Alta de gasto
    private final TextField tfCantidad;
    private final DatePicker dpFecha;
    private final ComboBox<Categoria> cbCategoria;
    private final TextField tfNota;
    private final Button btnAdd;
    private final Button btnFiltrar;

    // Tabla
    private final TableView<GastoRow> table;
    private final ObservableList<GastoRow> rows;

    // Estado inferior
    private final Label lblTotal;
    private final Label lblError;

    public VistaMain() {
        this.ctrl = Controlador.getInstance();
        this.root = new BorderPane();
        this.root.setPadding(new Insets(12));

        // ---- Cabecera: título + menú ----
        Label title = new Label("Gestión de Gastos");
        title.setFont(Font.font(18));

        MenuBar bar = new MenuBar();
        Menu menuDatos = new Menu("Datos");
        MenuItem miCategorias = new MenuItem("Categorías…");
        Menu menuVer = new Menu("Ver");
        MenuItem miCalendario = new MenuItem("Calendario de gastos…");
        menuDatos.getItems().addAll(miCategorias);
        bar.getMenus().add(menuDatos);
        menuVer.getItems().add(miCalendario);
        bar.getMenus().add(menuVer);
        
        VBox header = new VBox(6, title, bar);
        header.setAlignment(Pos.CENTER_LEFT);
        root.setTop(header);

        // ---- Panel superior: alta + botón Filtrar ----
        GridPane top = new GridPane();
        top.setHgap(8);
        top.setVgap(8);
        top.setPadding(new Insets(8));

        tfCantidad = new TextField();
        dpFecha = new DatePicker(LocalDate.now());
        cbCategoria = new ComboBox<>(FXCollections.observableArrayList(ctrl.listarCategorias()));
        tfNota = new TextField();
        btnAdd = new Button("Añadir gasto");
        btnFiltrar = new Button("Filtrar…");

        int r = 0;
        top.add(new Label("Cantidad (€):"), 0, r);
        top.add(tfCantidad, 1, r);
        top.add(new Label("Fecha:"), 2, r);
        top.add(dpFecha, 3, r);
        top.add(btnFiltrar, 4, r);

        r++;
        top.add(new Label("Categoría:"), 0, r);
        top.add(cbCategoria, 1, r);
        top.add(new Label("Nota:"), 2, r);
        top.add(tfNota, 3, r);
        top.add(btnAdd, 4, r);

        // ---- Tabla de gastos ----
        table = new TableView<>();
        rows = FXCollections.observableArrayList();

        TableColumn<GastoRow, LocalDate> cFecha = new TableColumn<>("Fecha");
        cFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cFecha.setPrefWidth(120);

        TableColumn<GastoRow, String> cCat = new TableColumn<>("Categoría");
        cCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        cCat.setPrefWidth(180);

        TableColumn<GastoRow, BigDecimal> cCant = new TableColumn<>("Cantidad");
        cCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cCant.setPrefWidth(120);

        TableColumn<GastoRow, String> cNota = new TableColumn<>("Nota");
        cNota.setCellValueFactory(new PropertyValueFactory<>("nota"));
        cNota.setPrefWidth(300);

        table.getColumns().addAll(cFecha, cCat, cCant, cNota);
        table.setItems(rows);

        VBox center = new VBox(10, top, table);
        center.setPadding(new Insets(8));
        root.setCenter(center);

        // ---- Pie: errores + total ----
        lblError = new Label(" ");
        lblError.setStyle("-fx-text-fill: red;");

        lblTotal = new Label("Total: 0.00 €");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, lblError, spacer, lblTotal);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(8));
        root.setBottom(bottom);

        // ---- Cargar datos (después de crear lblTotal/lblError) ----
        cargarTabla(ctrl.listarGastos());

        // ---- Listeners ----
        btnAdd.setOnAction(e -> onAdd());
        btnFiltrar.setOnAction(e -> onOpenFiltro());
        miCategorias.setOnAction(e -> onOpenCategorias());
        miCalendario.setOnAction(e -> {
            VistaCalendario vc = new VistaCalendario();
            Utils.openDialog(root, "Calendario de Gastos", vc.getRoot(), 900, 700);
        });
    }

    public Parent getRoot() {
        return root;
    }

    // ======================================================
    // Acciones
    // ======================================================

    private void onAdd() {
        lblError.setText(" ");
        try {
            BigDecimal cantidad = new BigDecimal(tfCantidad.getText().trim());
            if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
            }

            LocalDate fecha = dpFecha.getValue();
            if (fecha == null) {
                throw new IllegalArgumentException("Selecciona una fecha.");
            }

            Categoria cat = cbCategoria.getValue();
            if (cat == null) {
                throw new IllegalArgumentException("Selecciona una categoría.");
            }

            String nota = tfNota.getText().trim();

            ctrl.registrarGasto(cantidad, fecha, cat.getId(), nota);

            tfCantidad.clear();
            tfNota.clear();
            tfCantidad.requestFocus();

            cargarTabla(ctrl.listarGastos());
        } catch (Exception ex) {
            lblError.setText("❌ " + ex.getMessage());
        }
    }

    private void onOpenFiltro() {
        FiltroGastosDialog dlg = new FiltroGastosDialog();
        Utils.openDialog(root, "Filtros de gastos", dlg.getRoot(), 420, 260);

        if (dlg.isOk()) {
            CriteriosFiltroGastos c = dlg.getCriterios();
            List<Gasto> filtrados = ctrl.filtrarGastos(c.getDesde(), c.getHasta(), c.getCategoriaId());
            cargarTabla(filtrados);
        }
    }

    private void onOpenCategorias() {
        VistaCategorias dlg = new VistaCategorias();
        Utils.openDialog(root, "Categorías", dlg.getRoot(), 520, 420);

        // refresca combo y tabla por si cambió algo
        cbCategoria.getItems().setAll(ctrl.listarCategorias());
        cargarTabla(ctrl.listarGastos());
    }

    // ======================================================
    // Datos / Helpers
    // ======================================================

    private void cargarTabla(final List<Gasto> gastos) {
        rows.setAll(
            gastos.stream()
                  .map(g -> new GastoRow(
                          g.getFecha(),
                          nombreCategoria(g.getCategoriaId()),
                          g.getCantidad(),
                          g.getNota()
                  ))
                  .toList()
        );

        BigDecimal total = gastos.stream()
                                 .map(Gasto::getCantidad)
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotal.setText(String.format("Total: %.2f €", total));
    }

    private String nombreCategoria(final java.util.UUID id) {
        return ctrl.listarCategorias()
                   .stream()
                   .filter(c -> c.getId().equals(id))
                   .findFirst()
                   .map(Categoria::getNombre)
                   .orElse("");
    }

    // ======================================================
    // DTO fila para TableView
    // ======================================================
    public static class GastoRow {
        private final LocalDate fecha;
        private final String categoria;
        private final BigDecimal cantidad;
        private final String nota;

        public GastoRow(final LocalDate fecha,
                        final String categoria,
                        final BigDecimal cantidad,
                        final String nota) {
            this.fecha = fecha;
            this.categoria = categoria;
            this.cantidad = cantidad;
            this.nota = nota;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public String getCategoria() {
            return categoria;
        }

        public BigDecimal getCantidad() {
            return cantidad;
        }

        public String getNota() {
            return nota;
        }
    }
}
