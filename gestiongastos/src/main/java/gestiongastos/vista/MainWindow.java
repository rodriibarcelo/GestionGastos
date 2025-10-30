package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MainWindow extends JFrame {

    private final Controlador ctrl;

    private JTable tabla;
    private GastosTableModel model;

    // === Campos de alta ===
    private JComboBox<Categoria> comboAltaCategoria;
    private JTextField txtCantidad;
    private JTextField txtFecha;
    private JTextField txtNota;
    private JButton btnAgregar;

    // === Botón de filtros (abre diálogo) ===
    private JButton btnFiltrar;

    // === Estado inferior ===
    private JLabel lblTotal;
    private JLabel lblError;

    public MainWindow() {
        this.ctrl = Controlador.getInstance();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Gestión de Gastos");
        setSize(900, 620);
        setLocationRelativeTo(null);

        configurarUI();
        crearMenu();
        cargarTabla();
    }

    private void configurarUI() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        // ----- Panel superior: solo alta + botón filtrar -----
        JPanel top = new JPanel(new GridBagLayout());
        content.add(top, BorderLayout.NORTH);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Alta de gasto
        gbc.gridx = 0; gbc.gridy = 0;
        top.add(new JLabel("Cantidad (€):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtCantidad = new JTextField(8);
        top.add(txtCantidad, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        top.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        txtFecha = new JTextField(10);
        txtFecha.setText(LocalDate.now().toString());
        top.add(txtFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        top.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        comboAltaCategoria = new JComboBox<>(ctrl.listarCategorias().toArray(new Categoria[0]));
        top.add(comboAltaCategoria, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        top.add(new JLabel("Nota:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        txtNota = new JTextField(15);
        top.add(txtNota, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        btnAgregar = new JButton("Añadir gasto");
        top.add(btnAgregar, gbc);
        btnAgregar.addActionListener(e -> onAgregar());

        // Botón Filtrar…
        gbc.gridheight = 1;
        gbc.gridx = 5; gbc.gridy = 0;
        btnFiltrar = new JButton("Filtrar…");
        top.add(btnFiltrar, gbc);
        btnFiltrar.addActionListener(e -> abrirDialogoFiltros());

        // ----- Tabla central -----
        model = new GastosTableModel();
        tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        content.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ----- Panel inferior -----
        JPanel bottom = new JPanel(new BorderLayout());
        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        bottom.add(lblError, BorderLayout.WEST);

        lblTotal = new JLabel("Total: 0.00 €", SwingConstants.RIGHT);
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        bottom.add(lblTotal, BorderLayout.EAST);

        content.add(bottom, BorderLayout.SOUTH);
    }

    private void cargarTabla() {
        List<Gasto> gastos = ctrl.listarGastos();
        model.setData(gastos, ctrl.listarCategorias());
        actualizarTotal(gastos);
        lblError.setText(" ");
    }

    private void onAgregar() {
        lblError.setText(" ");
        try {
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());
            if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
            }

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            if (fecha.isAfter(LocalDate.now())) {
                lblError.setText("⚠ La fecha está en el futuro (se permite).");
            }

            Categoria cat = (Categoria) comboAltaCategoria.getSelectedItem();
            if (cat == null) {
                throw new IllegalArgumentException("Selecciona una categoría.");
            }

            String nota = txtNota.getText().trim();
            ctrl.registrarGasto(cantidad, fecha, cat.getId(), nota);

            // limpiar
            txtCantidad.setText("");
            txtNota.setText("");
            txtCantidad.requestFocus();

            cargarTabla();
        } catch (Exception ex) {
            lblError.setText("❌ " + ex.getMessage());
        }
    }

    private void abrirDialogoFiltros() {
        FiltroGastosDialog dlg = new FiltroGastosDialog(this);
        dlg.setVisible(true);

        if (dlg.isOk()) {
            CriteriosFiltroGastos c = dlg.getCriterios();

            List<Gasto> filtrados = ctrl.filtrarGastos(
                    c.getDesde(),
                    c.getHasta(),
                    c.getCategoriaId()
            );

            model.setData(filtrados, ctrl.listarCategorias());
            actualizarTotal(filtrados);
        }
    }

    private void actualizarTotal(final List<Gasto> gastos) {
        BigDecimal total = gastos.stream()
                .map(Gasto::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotal.setText(String.format("Total: %.2f €", total));
    }

    private void crearMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu datos = new JMenu("Datos");
        JMenuItem miCategorias = new JMenuItem("Categorías…");
        datos.add(miCategorias);
        bar.add(datos);
        setJMenuBar(bar);

        miCategorias.addActionListener(e -> {
            CategoriasDialog dlg = new CategoriasDialog(this);
            dlg.setVisible(true);
            recargarCategorias();
            cargarTabla();
        });
    }

    private void recargarCategorias() {
        comboAltaCategoria.removeAllItems();
        for (Categoria c : ctrl.listarCategorias()) {
            comboAltaCategoria.addItem(c);
        }
    }

    // ===== TABLE MODEL =====
    private static class GastosTableModel extends AbstractTableModel {

        private List<Gasto> gastos;
        private List<Categoria> categorias;
        private final String[] cols = new String[] { "Fecha", "Categoría", "Cantidad", "Nota" };

        public void setData(final List<Gasto> gastos, final List<Categoria> categorias) {
            this.gastos = gastos;
            this.categorias = categorias;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return gastos == null ? 0 : gastos.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(final int column) {
            return cols[column];
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            Gasto g = gastos.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return g.getFecha();
                case 1:
                    return nombreCategoria(g.getCategoriaId());
                case 2:
                    return g.getCantidad();
                case 3:
                    return g.getNota();
                default:
                    return "";
            }
        }

        private String nombreCategoria(final UUID id) {
            if (categorias == null) {
                return "";
            }
            return categorias.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .map(Categoria::getNombre)
                    .orElse("");
        }
    }
}
