package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FiltroGastosDialog extends JDialog {

    private JTextField txtDesde;
    private JTextField txtHasta;
    private JComboBox<Categoria> comboCategoria;
    private JButton btnAplicar;
    private JButton btnCancelar;
    private JLabel lblError;

    private boolean ok;
    private CriteriosFiltroGastos criterios;

    public FiltroGastosDialog(final Frame owner) {
        super(owner, "Filtros de gastos", true);
        configurarUI();
        cargarCategorias();
        setSize(420, 260);
        setLocationRelativeTo(owner);
    }

    private void configurarUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel(new GridBagLayout());
        content.add(form, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Desde (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtDesde = new JTextField(15);
        form.add(txtDesde, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Hasta (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtHasta = new JTextField(15);
        form.add(txtHasta, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        comboCategoria = new JComboBox<>();
        form.add(comboCategoria, gbc);

        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        content.add(lblError, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnCancelar = new JButton("Cancelar");
        btnAplicar = new JButton("Aplicar");
        botones.add(btnCancelar);
        botones.add(btnAplicar);
        content.add(botones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> {
            ok = false;
            dispose();
        });

        btnAplicar.addActionListener(e -> onAplicar());

        getRootPane().setDefaultButton(btnAplicar);
    }

    private void cargarCategorias() {
        List<Categoria> categorias = Controlador.getInstance().listarCategorias();
        comboCategoria.addItem(null); // "todas"
        for (Categoria c : categorias) {
            comboCategoria.addItem(c);
        }
    }

    private void onAplicar() {
        lblError.setText(" ");

        CriteriosFiltroGastos c = new CriteriosFiltroGastos();

        if (!txtDesde.getText().trim().isEmpty()) {
            try {
                c.setDesde(LocalDate.parse(txtDesde.getText().trim()));
            } catch (Exception e) {
                lblError.setText("Fecha 'desde' no válida.");
                return;
            }
        }

        if (!txtHasta.getText().trim().isEmpty()) {
            try {
                c.setHasta(LocalDate.parse(txtHasta.getText().trim()));
            } catch (Exception e) {
                lblError.setText("Fecha 'hasta' no válida.");
                return;
            }
        }

        Categoria seleccionada = (Categoria) comboCategoria.getSelectedItem();
        if (seleccionada != null) {
            c.setCategoriaId(seleccionada.getId());
        }

        this.criterios = c;
        this.ok = true;
        dispose();
    }

    public boolean isOk() {
        return ok;
    }

    public CriteriosFiltroGastos getCriterios() {
        return criterios;
    }
}
