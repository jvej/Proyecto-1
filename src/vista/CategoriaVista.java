package vista;

import controlador.CategoriaControlador;
import modelo.Categoria;
import util.PDFReportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaPanel extends JPanel {

    private final CategoriaControlador controlador = new CategoriaControlador();

    private JTextField txtBuscar;
    private JTextField txtId;
    private JTextField txtDescripcion;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public CategoriaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.add(construirPanelFormulario(), BorderLayout.NORTH);
        centro.add(construirPanelTabla(), BorderLayout.CENTER);

        add(construirPanelBusqueda(), BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        cargarTabla(controlador.listar());
    }

    private JPanel construirPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Busqueda"));

        panel.add(new JLabel("Descripcion:"));
        txtBuscar = new JTextField(20);
        panel.add(txtBuscar);

        JButton btnBuscar = new JButton("🔍 Buscar");
        JButton btnImprimir = new JButton("🖨 Imprimir");
        panel.add(btnBuscar);
        panel.add(btnImprimir);

        btnBuscar.addActionListener(e -> cargarTabla(controlador.buscar(txtBuscar.getText())));
        btnImprimir.addActionListener(e -> imprimirReporte());

        return panel;
    }

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Categoria"));

        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panel.add(txtId);

        panel.add(new JLabel("Descripcion:"));
        txtDescripcion = new JTextField();
        panel.add(txtDescripcion);

        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnGuardar = new JButton("💾 Guardar");
        JButton btnBorrar = new JButton("🗑 Borrar");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        botones.add(btnGuardar);
        botones.add(btnBorrar);
        botones.add(btnLimpiar);
        panel.add(botones);

        btnGuardar.addActionListener(e -> guardar());
        btnBorrar.addActionListener(e -> borrar());
        btnLimpiar.addActionListener(e -> limpiar());

        return panel;
    }

    private JScrollPane construirPanelTabla() {
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Descripcion"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtDescripcion.setText(modeloTabla.getValueAt(fila, 1).toString());
            }
        });
        return new JScrollPane(tabla);
    }

    private void cargarTabla(List<Categoria> lista) {
        modeloTabla.setRowCount(0);
        for (Categoria c : lista) {
            modeloTabla.addRow(new Object[]{c.getId(), c.getDescripcion()});
        }
    }

    private void guardar() {
        try {
            String id = txtId.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (id.isEmpty()) {
                controlador.crear(descripcion);
                JOptionPane.showMessageDialog(this, "Categoría creada correctamente.");
            } else {
                controlador.modificar(id, descripcion);
                JOptionPane.showMessageDialog(this, "Categoría actualizada correctamente.");
            }

            limpiar();
            cargarTabla(controlador.listar());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría de la lista.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea borrar esta categoría?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            controlador.eliminar(id);
            limpiar();
            cargarTabla(controlador.listar());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtDescripcion.setText("");
        txtBuscar.setText("");
        tabla.clearSelection();
    }

    private void imprimirReporte() {
        List<Categoria> lista = controlador.listar();
        List<String[]> filas = new ArrayList<>();
        for (Categoria c : lista) {
            filas.add(new String[]{c.getId(), c.getDescripcion()});
        }
        PDFReportUtil.generarReporteTabla(this, "Listado de Categorias", new String[]{"ID", "Descripcion"}, filas);
    }
}