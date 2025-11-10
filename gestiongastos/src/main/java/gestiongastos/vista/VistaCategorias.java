package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
        this.root.setPadding(new Insets(10));

        Label title = new Label("Categorías");
        root.setTop(title);

        table = new TableView<>();
        data = FXCollections.observableArrayList();
        cargarDatos();

        TableColumn<Categoria, String> cNombre = new TableColumn<>("Nombre");
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cNombre.setPrefWidth(220);

        TableColumn<Categoria, String> cColor = new TableColumn<>("Color (hex)");
        cColor.setCellValueFactory(new PropertyValueFactory<>("colorHex"));
        cColor.setPrefWidth(140);

        table.getColumns().addAll(cNombre, cColor);
        table.setItems(data);

        root.setCenter(table);

        btnAdd = new Button("Añadir");
        btnEdit = new Button("Editar");
        btnDelete = new Button("Borrar");
        btnCerrar = new Button("Cerrar");

        HBox bottom = new HBox(8, btnAdd, btnEdit, btnDelete, new Separator(), btnCerrar);
        bottom.setPadding(new Insets(10));
        root.setBottom(bottom);

        // Listeners
        btnCerrar.setOnAction(e -> root.getScene().getWindow().hide());
        btnAdd.setOnAction(e -> onAdd());
        btnEdit.setOnAction(e -> onEdit());
        btnDelete.setOnAction(e -> onDelete());
    }

    public Parent getRoot() {
        return root;
    }

    private void cargarDatos() {
        List<Categoria> cats = ctrl.listarCategorias();
        data.setAll(cats);
    }

    private void onAdd() {
        VistaCategoriaForm form = new VistaCategoriaForm(null);
        Utils.openDialog(root, "Nueva categoría", form.getRoot(), 360, 180);
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
        Utils.openDialog(root, "Editar categoría", form.getRoot(), 360, 180);
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
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar \"" + sel.getNombre() + "\"?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                ctrl.borrarCategoria(sel.getId());
                cargarDatos();
            }
        });
    }
}
