package vista;

import controlador.FuncionarioControlador;
import modelo.Funcionario;
import util.PDFReportUtil;
import util.CategoriaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioPanel extends JPanel {

    private final FuncionarioControlador controlador = new FuncionarioControlador();

    private JTextField txtBuscar;
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public FuncionarioPanel() {
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

        panel.add(new JLabel("ID o Nombre:"));
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
        panel.setBorder(BorderFactory.createTitledBorder("Funcionario"));

        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        panel.add(new JLabel("Telefono:"));
        txtTelefono = new JTextField();
        panel.add(txtTelefono);

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
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Nombre", "Telefono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtTelefono.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtId.setEditable(false);
            }
        });
        return new JScrollPane(tabla);
    }

    private void cargarTabla(List<Funcionario> lista) {
        modeloTabla.setRowCount(0);
        for (Funcionario f : lista) {
            modeloTabla.addRow(new Object[]{f.getId(), f.getNombre(), f.getTelefono()});
        }
    }

    private void guardar() {
        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (controlador.buscar(id).stream().noneMatch(f -> f.getId().equalsIgnoreCase(id))) {
                controlador.crear(id, nombre, telefono);
                JOptionPane.showMessageDialog(this, "Funcionario creado correctamente.");
            } else {
                controlador.modificar(id, nombre, telefono);
                JOptionPane.showMessageDialog(this, "Funcionario actualizado correctamente.");
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
            JOptionPane.showMessageDialog(this, "Seleccione un funcionario de la lista.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea borrar el funcionario " + id + "?",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            controlador.eliminar(id);
            limpiar();
            cargarTabla(controlador.listar());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtBuscar.setText("");
        tabla.clearSelection();
        txtId.setEditable(true);
    }

    private void imprimirReporte() {
        List<Funcionario> lista = controlador.listar();
        List<String[]> filas = new ArrayList<>();
        for (Funcionario f : lista) {
            filas.add(new String[]{f.getId(), f.getNombre(), f.getTelefono()});
        }
        PDFReportUtil.generarReporteTabla(this, "Listado de Funcionarios",
                new String[]{"ID", "Nombre", "Telefono"}, filas);
    }
}