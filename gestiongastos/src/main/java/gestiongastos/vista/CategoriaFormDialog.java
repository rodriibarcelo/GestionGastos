package gestiongastos.vista;

import gestiongastos.dominio.Categoria;

import javax.swing.*;
import java.awt.*;

public class CategoriaFormDialog extends JDialog {

    private JTextField txtNombre;
    private JTextField txtColor; // hex opcional, ej: #33AA77
    private JButton btnOk;
    private JButton btnCancel;

    private boolean ok;
    private Categoria result;

    public CategoriaFormDialog(final Dialog owner, final Categoria original) {
        super(owner, "Categoría", true);
        configurarUI();
        if (original != null) {
            txtNombre.setText(original.getNombre());
            txtColor.setText(original.getColorHex());
        }
        setSize(360, 180);
        setLocationRelativeTo(owner);
    }

    private void configurarUI() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel(new GridBagLayout());
        content.add(form, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtNombre = new JTextField(18);
        form.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Color (hex):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtColor = new JTextField(12);
        form.add(txtColor, gbc);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnOk = new JButton("Aceptar");
        btnCancel = new JButton("Cancelar");
        botones.add(btnCancel);
        botones.add(btnOk);
        content.add(botones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnOk);

        btnCancel.addActionListener(e -> {
            ok = false;
            dispose();
        });

        btnOk.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "El nombre no puede estar vacío.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            result = new Categoria(txtNombre.getText().trim(), txtColor.getText().trim());
            ok = true;
            dispose();
        });
    }

    public boolean isOk() {
        return ok;
    }

    public Categoria getResult() {
        return result;
    }
}
