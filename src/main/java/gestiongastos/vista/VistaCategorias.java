package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.List;

public class VistaCategorias {

    private final BorderPane root;
    private final Controlador ctrl;
    private final TableView<Categoria> table;
    private final ObservableList<Categoria> data;

    private final Button btnAdd;
    private final Button btnEdit;
    private final Button btnDelete;
    private final Button btnCerrar;

    public VistaCategorias() {
        this.ctrl = Controlador.getInstance();

        this.root = new BorderPane();
        this.root.setPadding(new Insets(12));
        this.root.setPrefSize(560, 420);

        // --- Título ---
        Label title = new Label("Categorías");
        title.setFont(Font.font(20));
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setMargin(title, new Insets(4, 0, 8, 0));
        root.setTop(title);

        // --- Tabla ---
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No hay categorías."));
        data = FXCollections.observableArrayList();
        cargarDatos();
        table.setItems(data);

        TableColumn<Categoria, String> cNombre = new TableColumn<>("Nombre");
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cNombre.setMinWidth(200);

        TableColumn<Categoria, String> cColorTexto = new TableColumn<>("Color (hex)");
        cColorTexto.setCellValueFactory(new PropertyValueFactory<>("colorHex"));
        cColorTexto.setMinWidth(140);

        TableColumn<Categoria, String> cColorSwatch = new TableColumn<>(" ");
        cColorSwatch.setMinWidth(60);
        cColorSwatch.setMaxWidth(70);
        cColorSwatch.setReorderable(false);
        cColorSwatch.setSortable(false);
        cColorSwatch.setCellValueFactory(new PropertyValueFactory<>("colorHex"));
        cColorSwatch.setCellFactory(col -> new TableCell<>() {
            private final Rectangle rect = new Rectangle(28, 16);
            {
                rect.setArcWidth(6);
                rect.setArcHeight(6);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(Pos.CENTER);
            }
            @Override protected void updateItem(String hex, boolean empty) {
                super.updateItem(hex, empty);
                if (empty || hex == null || hex.isBlank()) {
                    setGraphic(null);
                } else {
                    try {
                        rect.setFill(Color.web(hex));
                        rect.setStroke(Color.gray(0.75));
                        setGraphic(rect);
                    } catch (IllegalArgumentException ex) {
                        rect.setFill(Color.TRANSPARENT);
                        setGraphic(rect);
                    }
                }
            }
        });

        table.getColumns().setAll(cNombre, cColorTexto, cColorSwatch);
        VBox center = new VBox(table);
        center.setPadding(new Insets(4, 0, 8, 0));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(center);

        btnAdd = new Button("Añadir");
        btnEdit = new Button("Editar");
        btnDelete = new Button("Borrar");
        btnCerrar = new Button("Cerrar");

        btnEdit.disableProperty().bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));
        btnDelete.disableProperty().bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bottom = new HBox(8, btnAdd, btnEdit, btnDelete, spacer, btnCerrar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        btnCerrar.setOnAction(e -> root.getScene().getWindow().hide());
        btnAdd.setOnAction(e -> onAdd());
        btnEdit.setOnAction(e -> onEdit());
        btnDelete.setOnAction(e -> onDelete());

        table.setRowFactory(tv -> {
            TableRow<Categoria> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) onEdit();
            });
            return row;
        });
    }

    public Parent getRoot() { return root; }

    private void cargarDatos() {
        List<Categoria> cats = ctrl.listarCategorias();
        data.setAll(cats);
    }

    private void onAdd() {
        VistaCategoriaForm form = new VistaCategoriaForm(null);
        Utils.openDialog(root, "Nueva categoría", form.getRoot(), 380, 200);
        if (form.isOk()) {
            ctrl.crearCategoria(form.getNombre(), form.getColorHex());
            cargarDatos();
        }
    }

    private void onEdit() {
        Categoria sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utils.alertWarn("Selecciona una categoría.");
            return;
        }
        VistaCategoriaForm form = new VistaCategoriaForm(sel);
        Utils.openDialog(root, "Editar categoría", form.getRoot(), 380, 200);
        if (form.isOk()) {
            ctrl.actualizarCategoria(sel.getId(), form.getNombre(), form.getColorHex());
            cargarDatos();
        }
    }

    private void onDelete() {
        Categoria sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Utils.alertWarn("Selecciona una categoría.");
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Borrar \"" + sel.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                ctrl.borrarCategoria(sel.getId());
                cargarDatos();
            }
        });
    }
}
