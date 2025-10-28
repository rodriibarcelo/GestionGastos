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

    private JComboBox<Categoria> comboCategorias;
    private JTextField txtCantidad;
    private JTextField txtFecha;
    private JTextField txtNota;
    private JButton btnAgregar;

    public MainWindow() {
        this.ctrl = Controlador.getInstance();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configurarUI();
        crearMenu();
        cargarTabla();
    }



    private void configurarUI() {
        setTitle("Gestión de Gastos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        // Top: formulario rápido de alta
        JPanel top = new JPanel(new GridBagLayout());
        content.add(top, BorderLayout.NORTH);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
        comboCategorias = new JComboBox<>(ctrl.listarCategorias().toArray(new Categoria[0]));
        top.add(comboCategorias, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        top.add(new JLabel("Nota:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        txtNota = new JTextField(15);
        top.add(txtNota, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        btnAgregar = new JButton("Añadir gasto");
        top.add(btnAgregar, gbc);

        btnAgregar.addActionListener(e -> onAgregar());

        // Center: tabla
        model = new GastosTableModel();
        tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        content.add(new JScrollPane(tabla), BorderLayout.CENTER);
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
            recargarCategoriasEnCombo();
            cargarTabla();
        });
    }


    private void recargarCategoriasEnCombo() {
        comboCategorias.removeAllItems();
        for (Categoria c : ctrl.listarCategorias()) {
            comboCategorias.addItem(c);
        }
    }


    private void cargarTabla() {
        model.setData(ctrl.listarGastos(), ctrl.listarCategorias());
    }

    private void onAgregar() {
        try {
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            Categoria cat = (Categoria) comboCategorias.getSelectedItem();
            String nota = txtNota.getText().trim();

            if (cat == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ctrl.registrarGasto(cantidad, fecha, cat.getId(), nota);
            cargarTabla();
            txtCantidad.setText("");
            txtNota.setText("");
            txtCantidad.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos no válidos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== TableModel =====
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
