package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FiltroGastosDialog {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final BorderPane root;

    private final DatePicker dpDesde;
    private final DatePicker dpHasta;
    // Mantenemos Object para admitir "Todas" (String) o Categoria
    private final ComboBox<Object> cbCategoria;

    private boolean ok;
    private CriteriosFiltroGastos criterios;

    public FiltroGastosDialog() {
        this.root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setPrefSize(420, 240);

        // --- Título ---
        Label title = new Label("Filtros de gastos");
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setMargin(title, new Insets(0, 0, 8, 0));
        root.setTop(title);

        // --- Formulario centrado ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(8));
        form.setAlignment(Pos.CENTER);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHalignment(HPos.RIGHT);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        dpDesde = new DatePicker();
        dpHasta = new DatePicker();

        
        dpDesde.setConverter(new StringConverter<>() {
            @Override public String toString(LocalDate d) { return d == null ? "" : DF.format(d); }
            @Override public LocalDate fromString(String s) { return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DF); }
        });
        dpHasta.setConverter(new StringConverter<>() {
            @Override public String toString(LocalDate d) { return d == null ? "" : DF.format(d); }
            @Override public LocalDate fromString(String s) { return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DF); }
        });

        cbCategoria = new ComboBox<>();
        cbCategoria.setItems(FXCollections.observableArrayList());
        cbCategoria.getItems().add("Todas");
        List<Categoria> cats = Controlador.getInstance().listarCategorias();
        cbCategoria.getItems().addAll(cats);
        cbCategoria.getSelectionModel().selectFirst();
        cbCategoria.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else if (item instanceof String s) { setText(s); }
                else if (item instanceof Categoria c) { setText(c.getNombre()); }
            }
        });
        cbCategoria.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else if (item instanceof String s) { setText(s); }
                else if (item instanceof Categoria c) { setText(c.getNombre()); }
            }
        });

        form.add(new Label("Desde:"),     0, 0); form.add(dpDesde,     1, 0);
        form.add(new Label("Hasta:"),     0, 1); form.add(dpHasta,     1, 1);
        form.add(new Label("Categoría:"), 0, 2); form.add(cbCategoria, 1, 2);

        root.setCenter(form);

        // Botones derecha
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setCancelButton(true);
        Button btnAplicar  = new Button("Aplicar");
        btnAplicar.setDefaultButton(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bottom = new HBox(10, spacer, btnCancelar, btnAplicar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        // --- Acciones ---
        btnCancelar.setOnAction(e -> {
            ok = false;
            root.getScene().getWindow().hide();
        });
        btnAplicar.setOnAction(e -> onAplicar());
    }

    private void onAplicar() {
        LocalDate d1 = dpDesde.getValue();
        LocalDate d2 = dpHasta.getValue();

        // Si ambas fechas están y d1 > d2, intercambiar
        if (d1 != null && d2 != null && d1.isAfter(d2)) {
            LocalDate tmp = d1; d1 = d2; d2 = tmp;
        }

        CriteriosFiltroGastos c = new CriteriosFiltroGastos();
        c.setDesde(d1);
        c.setHasta(d2);

        Object sel = cbCategoria.getSelectionModel().getSelectedItem();
        if (sel instanceof Categoria cat) {
            c.setCategoriaId(cat.getId());
        } else {
            c.setCategoriaId(null); // "Todas"
        }

        this.criterios = c;
        this.ok = true;
        root.getScene().getWindow().hide();
    }

    public Parent getRoot() { return root; }

    public boolean isOk() { return ok; }

    public CriteriosFiltroGastos getCriterios() { return criterios; }
}
