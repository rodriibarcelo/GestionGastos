package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Notificacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class VistaNotificaciones {

    private static final DateTimeFormatter DFH = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BorderPane root;
    private final Controlador ctrl;

    private final TableView<NotificacionRow> table;
    private final ObservableList<NotificacionRow> rows;

    public VistaNotificaciones() {
        this.ctrl = Controlador.getInstance();
        this.root = new BorderPane();
        this.root.setPadding(new Insets(12));

        Label titulo = new Label("Historial de notificaciones");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(0, 0, 10, 0));

        table = new TableView<>();
        rows = FXCollections.observableArrayList();
        table.setItems(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<NotificacionRow, LocalDateTime> cFecha = new TableColumn<>("Fecha");
        cFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cFecha.setMinWidth(160);
        cFecha.setMaxWidth(200);
        cFecha.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DFH.format(item));
            }
        });

        TableColumn<NotificacionRow, String> cMensaje = new TableColumn<>("Mensaje");
        cMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));

        table.getColumns().setAll(cFecha, cMensaje);

        root.setCenter(table);

        Button btnRefrescar = new Button("Refrescar");
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setCancelButton(true);

        btnRefrescar.setOnAction(e -> cargar());
        btnCerrar.setOnAction(e -> root.getScene().getWindow().hide());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, btnRefrescar, spacer, btnCerrar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        cargar();
    }

    public Parent getRoot() {
        return root;
    }

    private void cargar() {
        rows.clear();

        var notifs = ctrl.listarNotificaciones();
        notifs.stream()
                .sorted(Comparator.comparing(Notificacion::getFecha).reversed())
                .forEach(n -> rows.add(new NotificacionRow(n.getFecha(), n.getMensaje())));

        if (rows.isEmpty()) {
            table.setPlaceholder(new Label("No hay notificaciones."));
        }
    }

    public static class NotificacionRow {
        private final LocalDateTime fecha;
        private final String mensaje;

        public NotificacionRow(LocalDateTime fecha, String mensaje) {
            this.fecha = fecha;
            this.mensaje = mensaje;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
