package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriasDialog extends JDialog {

    private final Controlador ctrl;
    private JTable tabla;
    private CategoriaTableModel model;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnCerrar;

    public CategoriasDialog(final Frame owner) {
        super(owner, "Categorías", true);
        this.ctrl = Controlador.getInstance();
        configurarUI();
        cargarTabla();
        setSize(520, 420);
        setLocationRelativeTo(owner);
    }

    private void configurarUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        model = new CategoriaTableModel();
        tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        content.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnAdd = new JButton("Añadir");
        btnEdit = new JButton("Editar");
        btnDelete = new JButton("Borrar");
        btnCerrar = new JButton("Cerrar");
        botones.add(btnAdd);
        botones.add(btnEdit);
        botones.add(btnDelete);
        botones.add(btnCerrar);
        content.add(botones, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarTabla() {
        model.setData(new ArrayList<>(ctrl.listarCategorias()));
    }

    private void onAdd() {
        CategoriaFormDialog form = new CategoriaFormDialog(this, null);
        form.setVisible(true);
        if (form.isOk()) {
            Categoria creada = form.getResult();
            // El form crea una Categoria, pero necesitamos pasar por el servicio:
            ctrl.crearCategoria(creada.getNombre(), creada.getColorHex());
            cargarTabla();
        }
    }

    private void onEdit() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona una categoría.",
                "Editar",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        Categoria seleccionada = model.getAt(row);
        CategoriaFormDialog form = new CategoriaFormDialog(this, seleccionada);
        form.setVisible(true);
        if (form.isOk()) {
            // Como el ID no cambia y el repo es en memoria, bastará con sobrescribir:
            // (Para simplicidad: eliminar y volver a guardar)
            ctrl.actualizarCategoria(seleccionada.getId(), form.getResult().getNombre(), form.getResult().getColorHex());
            cargarTabla();
        }
    }

    private void onDelete() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona una categoría.",
                "Borrar",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        Categoria seleccionada = model.getAt(row);
        int resp = JOptionPane.showConfirmDialog(
            this,
            "¿Borrar la categoría \"" + seleccionada.getNombre() + "\"?",
            "Confirmar borrado",
            JOptionPane.YES_NO_OPTION
        );
        if (resp == JOptionPane.YES_OPTION) {
            ctrl.borrarCategoria(seleccionada.getId());
            cargarTabla();
        }
    }

    // ===== TableModel =====
    private static class CategoriaTableModel extends AbstractTableModel {

        private final String[] cols = new String[] { "Nombre", "Color" };
        private List<Categoria> data = new ArrayList<>();

        public void setData(final List<Categoria> categorias) {
            this.data = categorias;
            fireTableDataChanged();
        }

        public Categoria getAt(final int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() {
            return data == null ? 0 : data.size();
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
            Categoria c = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return c.getNombre();
                case 1:
                    return c.getColorHex();
                default:
                    return "";
            }
        }
    }
}
