package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.CuentaCompartida;
import gestiongastos.dominio.Participacion;
import gestiongastos.dominio.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

public class VistaCuentaForm {

    private final BorderPane root;
    private boolean ok = false;

    private final TextField tfNombre;
    private final VBox listaUsuarios;

    private final Controlador ctrl = Controlador.getInstance();

    // Para recuperar datos tras aceptar:
    private final Map<UUID, CheckBox> mapaChecks = new HashMap<>();
    private final Map<UUID, TextField> mapaPorcentajes = new HashMap<>();

    public VistaCuentaForm(CuentaCompartida original) {
        root = new BorderPane();
        root.setPadding(new Insets(16));

        // ------------------------------------------
        // CONTENEDOR CENTRAL (nombre + lista usuarios)
        // ------------------------------------------
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(0));
        root.setCenter(contenido);

        // --- Fila nombre de la cuenta ---
        HBox filaNombre = new HBox(8);
        filaNombre.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label("Nombre de la cuenta:");
        tfNombre = new TextField();
        tfNombre.setPromptText("Ej. Viaje a Madrid");
        tfNombre.setPrefWidth(260);

        if (original != null) {
            tfNombre.setText(original.getNombre());
        }

        filaNombre.getChildren().addAll(lblNombre, tfNombre);
        contenido.getChildren().add(filaNombre);

        // ------------------------------------------
        // LISTA DE USUARIOS CON CHECK + PORCENTAJE
        // ------------------------------------------
        listaUsuarios = new VBox(10);
        listaUsuarios.setPadding(new Insets(10));

        construirListaUsuarios(original);

        ScrollPane scroll = new ScrollPane(listaUsuarios);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(260);

        contenido.getChildren().add(scroll);

        // ------------------------------------------
        // BOTONES
        // ------------------------------------------
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setCancelButton(true);

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, spacer, btnCancelar, btnAceptar);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        bottom.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(bottom);

        btnCancelar.setOnAction(e -> root.getScene().getWindow().hide());
        btnAceptar.setOnAction(e -> onAceptar());
    }

    public Parent getRoot() {
        return root;
    }

    public boolean isOk() {
        return ok;
    }

    public String getNombre() {
        return tfNombre.getText().trim();
    }

    public List<Participacion> getParticipaciones() {
        List<Participacion> list = new ArrayList<>();

        for (var entry : mapaChecks.entrySet()) {
            if (entry.getValue().isSelected()) {

                UUID userId = entry.getKey();
                String txt = mapaPorcentajes.get(userId).getText().trim();

                double porcentaje = txt.isBlank() ? 0 : Double.parseDouble(txt.replace(',', '.'));

                list.add(new Participacion(userId, null, porcentaje));
            }
        }

        return list;
    }

    // ============================================================
    // Construir lista de usuarios
    // ============================================================
    private void construirListaUsuarios(CuentaCompartida original) {

        List<Usuario> usuarios = ctrl.listarUsuarios();

        // Mapa para recuperar porcentaje si es edición
        Map<UUID, Double> porcentajesIniciales = new HashMap<>();

        if (original != null) {
            original.getParticipantes().forEach(p ->
                    porcentajesIniciales.put(p.getUsuarioId(), p.getPorcentaje())
            );
        }

        listaUsuarios.getChildren().clear();
        mapaChecks.clear();
        mapaPorcentajes.clear();

        for (Usuario u : usuarios) {

            CheckBox chk = new CheckBox(u.getNombreUsuario());
            mapaChecks.put(u.getId(), chk);

            TextField tf = new TextField();
            tf.setPromptText("%");
            tf.setPrefWidth(60);
            mapaPorcentajes.put(u.getId(), tf);

            // Habilitar/deshabilitar campo % según el check
            tf.disableProperty().bind(chk.selectedProperty().not());

            // Si estamos editando…
            if (porcentajesIniciales.containsKey(u.getId())) {
                chk.setSelected(true);
                tf.setText(String.valueOf(porcentajesIniciales.get(u.getId())));
            }

            HBox fila = new HBox(10, chk, tf);
            fila.setAlignment(Pos.CENTER_LEFT);

            listaUsuarios.getChildren().add(fila);
        }
    }


    // ============================================================
    // Aceptar formulario
    // ============================================================
    private void onAceptar() {

        if (tfNombre.getText().trim().isBlank()) {
            Utils.alertWarn("El nombre no puede estar vacío.");
            return;
        }

        double total = 0;
        int seleccionados = 0;
        boolean algunoConPorcentaje = false;

        for (var entry : mapaChecks.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionados++;

                String txt = mapaPorcentajes.get(entry.getKey())
                        .getText().trim();

                // Si el campo está vacío, lo tratamos como 0 (reparto equitativo posible)
                if (!txt.isBlank()) {

                    if (!txt.matches("\\d{1,3}([\\.,]\\d{1,2})?")) {
                        Utils.alertWarn("Formato de porcentaje inválido (usa números como 40 o 33.33).");
                        return;
                    }

                    double valor = Double.parseDouble(txt.replace(',', '.'));
                    total += valor;
                    algunoConPorcentaje = true;
                }
            }
        }

        if (seleccionados == 0) {
            Utils.alertWarn("Debe haber al menos un participante.");
            return;
        }

        // Si el usuario ha puesto porcentajes, la suma debe ser 100.
        // Si los deja todos en blanco, el servicio hará reparto equitativo.
        if (algunoConPorcentaje && Math.abs(total - 100.0) > 0.01) {
            Utils.alertWarn("La suma total de porcentajes debe ser 100% (actualmente es " + total + ").");
            return;
        }

        ok = true;
        root.getScene().getWindow().hide();
    }
}
