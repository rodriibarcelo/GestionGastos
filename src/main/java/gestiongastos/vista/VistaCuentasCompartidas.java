package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.CuentaCompartida;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class VistaCuentasCompartidas {

    private final BorderPane root;
    private final Controlador ctrl = Controlador.getInstance();

    private final TableView<CuentaCompartida> table;
    private final ObservableList<CuentaCompartida> data;

    private final Button btnAdd;
    private final Button btnEdit;
    private final Button btnCerrar;

    public VistaCuentasCompartidas() {

        root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setPrefSize(600, 450);

        // --- Título ---
        Label title = new Label("Cuentas Compartidas");
        title.setFont(Font.font(20));
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setMargin(title, new Insets(4, 0, 8, 0));
        root.setTop(title);

        // --- Tabla ---
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        data = FXCollections.observableArrayList(ctrl.getServicioCuentas().listar());
        table.setItems(data);

        TableColumn<CuentaCompartida, String> cNombre = new TableColumn<>("Nombre");
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cNombre.setMinWidth(200);

        TableColumn<CuentaCompartida, Integer> cNumMiembros = new TableColumn<>("Participantes");
        cNumMiembros.setCellValueFactory(cd ->
                new ReadOnlyObjectWrapper<>(cd.getValue().getParticipantes().size())
        );
        cNumMiembros.setMinWidth(100);

        table.getColumns().setAll(cNombre, cNumMiembros);

        VBox center = new VBox(table);
        center.setPadding(new Insets(4, 0, 8, 0));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(center);

        // --- Botones ---
        btnAdd = new Button("Añadir");
        btnEdit = new Button("Editar");
        btnCerrar = new Button("Cerrar");

        btnEdit.disableProperty()
                .bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(8, btnAdd, btnEdit, spacer, btnCerrar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        // --- Listeners ---
        btnAdd.setOnAction(e -> onAdd());
        btnEdit.setOnAction(e -> onEdit());
        btnCerrar.setOnAction(e -> root.getScene().getWindow().hide());

        table.setRowFactory(tv -> {
            TableRow<CuentaCompartida> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) onEdit();
            });
            return row;
        });
    }

    public Parent getRoot() {
        return root;
    }

    private void refresh() {
        data.setAll(ctrl.getServicioCuentas().listar());
    }

    private void onAdd() {
        VistaCuentaForm form = new VistaCuentaForm(null);
        Utils.openDialog(root, "Nueva cuenta compartida", form.getRoot(), 520, 520);
        if (form.isOk()) {
            ctrl.getServicioCuentas().crear(form.getNombre(), form.getParticipaciones());
            refresh();
        }
    }

    private void onEdit() {
        CuentaCompartida sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        VistaCuentaForm form = new VistaCuentaForm(sel);
        Utils.openDialog(root, "Editar cuenta", form.getRoot(), 520, 520);

        if (form.isOk()) {
            sel.setNombre(form.getNombre());
            sel.setParticipantes(form.getParticipaciones());
            ctrl.getServicioCuentas().actualizar(sel);
            refresh();
        }
    }
}
