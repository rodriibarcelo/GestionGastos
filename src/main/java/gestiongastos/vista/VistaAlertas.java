package gestiongastos.vista;

import gestiongastos.alertas.AlertaImporteMayor;
import gestiongastos.alertas.AlertaTotalDiario;
import gestiongastos.alertas.AlertaStrategy;
import gestiongastos.controlador.Controlador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VistaAlertas {

    private final BorderPane root;

    public VistaAlertas() {

        root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setPrefSize(380, 260);

        CheckBox cbImporte = new CheckBox("Avisar si un gasto supera...");
        TextField tfImporte = new TextField("100");

        CheckBox cbDiario = new CheckBox("Avisar si el total diario supera...");
        TextField tfDiario = new TextField("200");

        GridPane gp = new GridPane();
        gp.setVgap(12);
        gp.setHgap(10);
        gp.setPadding(new Insets(10));

        gp.add(cbImporte, 0, 0);
        gp.add(tfImporte, 1, 0);

        gp.add(cbDiario, 0, 1);
        gp.add(tfDiario, 1, 1);

        root.setCenter(gp);

        Button btnAceptar = new Button("Aceptar");
        Button btnCancelar = new Button("Cancelar");

        HBox bottom = new HBox(10, btnCancelar, btnAceptar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10));
        root.setBottom(bottom);

        btnCancelar.setOnAction(e -> root.getScene().getWindow().hide());

        btnAceptar.setOnAction(e -> {
            try {
                List<AlertaStrategy> estrategias = new ArrayList<>();

                if (cbImporte.isSelected()) {
                    BigDecimal limite = new BigDecimal(tfImporte.getText());
                    estrategias.add(new AlertaImporteMayor(limite));
                }

                if (cbDiario.isSelected()) {
                    BigDecimal limite = new BigDecimal(tfDiario.getText());
                    estrategias.add(new AlertaTotalDiario(limite));
                }

                Controlador.getInstance().configurarAlertas(estrategias);

                Utils.alertInfo("Alertas configuradas.");
                root.getScene().getWindow().hide();

            } catch (Exception ex) {
                Utils.alertError("Error: " + ex.getMessage());
            }
        });
    }

    public Parent getRoot() {
        return root;
    }
}
