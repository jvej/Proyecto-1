package vista;
import controlador.RecursoControlador;
import util.CategoriaService;
import modelo.Categoria;

import modelo.Recurso;
import util.PDFReportUtil;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedHashMap;

public class RecursoPanel extends JPanel {
    private final RecursoControlador controlador = new RecursoControlador();


    private final List<Categoria> categorias = new CategoriaService().listar();

    private JComboBox<String> cmbFiltroCategoria;
    private JTextField txtFiltroDescripcion;

    private JTextField txtId;
    private JComboBox<String> cmbCategoria;
    private JTextField txtDescripcion;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public RecursoPanel() {

        setLayout(null);

        JLabel lblFiltroCategoria = new JLabel("Categoría:");
        lblFiltroCategoria.setBounds(20, 15, 80, 25);
        add(lblFiltroCategoria);

        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.addItem(""); // "" = sin filtro, muestra todas
        for (Categoria c : categorias) {
            cmbFiltroCategoria.addItem(c.getDescripcion());
        }
        cmbFiltroCategoria.setBounds(100, 15, 180, 25);
        add(cmbFiltroCategoria);

        JLabel lblFiltroDescripcion = new JLabel("Descripción:");
        lblFiltroDescripcion.setBounds(300, 15, 80, 25);
        add(lblFiltroDescripcion);

        txtFiltroDescripcion = new JTextField();
        txtFiltroDescripcion.setBounds(385, 15, 150, 25);
        add(txtFiltroDescripcion);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(545, 15, 90, 25);
        add(btnBuscar);

        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.setBounds(645, 15, 100, 25);
        add(btnImprimir);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(20, 60, 100, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(140, 60, 180, 25);
        add(txtId);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBounds(20, 95, 100, 25);
        add(lblCategoria);

        cmbCategoria = new JComboBox<>();
        for (Categoria c : categorias) {
            cmbCategoria.addItem(c.getDescripcion());
        }
        cmbCategoria.setBounds(140, 95, 180, 25);
        add(cmbCategoria);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(20, 130, 100, 25);
        add(lblDescripcion);

        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(140, 130, 250, 25);
        add(txtDescripcion);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(20, 170, 100, 28);
        add(btnGuardar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(130, 170, 100, 28);
        add(btnBorrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(240, 170, 100, 28);
        add(btnLimpiar);


        JLabel lblListado = new JLabel("Listado:");
        lblListado.setBounds(20, 210, 100, 20);
        add(lblListado);

        modeloTabla = new DefaultTableModel(new Object[]{"Id", "Categoría", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 235, 720, 250);
        add(scroll);

        btnBuscar.addActionListener(e -> buscar());
        btnImprimir.addActionListener(e -> imprimir());
        btnGuardar.addActionListener(e -> guardar());
        btnBorrar.addActionListener(e -> borrar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                cargarFilaEnFormulario(tabla.getSelectedRow());
            }
        });

        cargarTabla(controlador.listar());
    }

    private void buscar() {
        String descripcionCategoria = (String) cmbFiltroCategoria.getSelectedItem();
        String categoriaId = idPorDescripcion(descripcionCategoria);
        String descripcion = txtFiltroDescripcion.getText().trim();
        cargarTabla(controlador.buscar(categoriaId, descripcion));
    }

    private void guardar() {
        try {
            String categoriaId = idPorDescripcion((String) cmbCategoria.getSelectedItem());
            controlador.guardar(txtId.getText(), categoriaId, txtDescripcion.getText());
            JOptionPane.showMessageDialog(this, "Recurso guardado correctamente.");
            limpiar();
            cargarTabla(controlador.listar());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrar() {
        try {
            controlador.borrar(txtId.getText());
            JOptionPane.showMessageDialog(this, "Recurso borrado.");
            limpiar();
            cargarTabla(controlador.listar());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtDescripcion.setText("");
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        tabla.clearSelection();
    }

    private void imprimir() {
        String[] columnas = {"Id", "Categoría", "Descripción"};
        List<String[]> filas = new ArrayList<>();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            filas.add(new String[]{
                    (String) modeloTabla.getValueAt(i, 0),
                    (String) modeloTabla.getValueAt(i, 1),
                    (String) modeloTabla.getValueAt(i, 2)
            });
        }
        PDFReportUtil.generarReporteTabla(this, "Listado de Recursos", columnas, filas);
    }

    private void cargarTabla(List<Recurso> recursos) {
        modeloTabla.setRowCount(0);
        for (Recurso r : recursos) {
            String descCategoria = r.getCategoriaId();
            for (Categoria c : categorias) {
                if (c.getId().equalsIgnoreCase(r.getCategoriaId())) {
                    descCategoria = c.getDescripcion();
                    break;
                }
            }
            modeloTabla.addRow(new Object[]{r.getId(), descCategoria, r.getDescripcion()});
        }
    }

    private void cargarFilaEnFormulario(int fila) {
        txtId.setText((String) modeloTabla.getValueAt(fila, 0));
        cmbCategoria.setSelectedItem(modeloTabla.getValueAt(fila, 1));
        txtDescripcion.setText((String) modeloTabla.getValueAt(fila, 2));
    }

    private String idPorDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isEmpty()) return null;
        for (Categoria c : categorias) {
            if (c.getDescripcion().equals(descripcion)) return c.getId();
        }
        return null;
    }
}
