package vista;

import controlador.CalendarizacionControlador;
import controlador.CalendarizacionControlador.Celda;
import dao.RecursoLecturaDAO.RecursoInfo;
import modelo.Categoria;
import util.PDFReportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarizacionPanel extends JPanel {

    private final CalendarizacionControlador controlador = new CalendarizacionControlador();

    private JComboBox<Categoria> comboCategoria;
    private JTextField txtFecha;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private List<RecursoInfo> recursosActuales = new ArrayList<>();

    public CalendarizacionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirPanelFiltros(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);
    }

    private JPanel construirPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));

        panel.add(new JLabel("Fecha (yyyy-MM-dd):"));
        txtFecha = new JTextField(10);
        panel.add(txtFecha);

        panel.add(new JLabel("Categoria:"));
        comboCategoria = new JComboBox<>();
        for (Categoria c : controlador.listarCategorias()) {
            comboCategoria.addItem(c);
        }
        comboCategoria.setPreferredSize(new Dimension(180, 25));
        panel.add(comboCategoria);

        JButton btnCargar = new JButton("✅ Cargar");
        JButton btnImprimir = new JButton("🖨 Imprimir");
        panel.add(btnCargar);
        panel.add(btnImprimir);

        btnCargar.addActionListener(e -> cargarMatriz());
        btnImprimir.addActionListener(e -> imprimirReporte());

        return panel;
    }

    private JScrollPane construirPanelTabla() {
        modeloTabla = new DefaultTableModel();
        tabla = new JTable(modeloTabla);
        tabla.setEnabled(false);
        return new JScrollPane(tabla);
    }

    private void cargarMatriz() {
        try {
            Categoria categoriaSeleccionada = (Categoria) comboCategoria.getSelectedItem();
            if (categoriaSeleccionada == null) {
                throw new Exception("No hay categorías registradas todavía.");
            }

            recursosActuales = controlador.obtenerRecursos(categoriaSeleccionada.getId());
            if (recursosActuales.isEmpty()) {
                throw new Exception("Esa categoría no tiene recursos registrados todavía.");
            }

            Map<String, Map<Integer, Celda>> matriz = controlador.obtenerMatriz(txtFecha.getText(), recursosActuales);

            // Columnas: "Hora" + una por cada recurso
            String[] columnas = new String[recursosActuales.size() + 1];
            columnas[0] = "Hora";
            for (int i = 0; i < recursosActuales.size(); i++) {
                columnas[i + 1] = recursosActuales.get(i).descripcion;
            }
            modeloTabla = new DefaultTableModel(columnas, 0);

            for (int hora = 6; hora <= 21; hora++) {
                Object[] fila = new Object[recursosActuales.size() + 1];
                fila[0] = String.format("%02d:00", hora);
                for (int i = 0; i < recursosActuales.size(); i++) {
                    Celda celda = matriz.get(recursosActuales.get(i).id).get(hora);
                    fila[i + 1] = celda == null ? "" : celda.actividad + " - " + celda.idFuncionario;
                }
                modeloTabla.addRow(fila);
            }
            tabla.setModel(modeloTabla);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirReporte() {
        if (modeloTabla.getColumnCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero cargue una calendarización.");
            return;
        }
        String[] columnas = new String[modeloTabla.getColumnCount()];
        for (int i = 0; i < columnas.length; i++) columnas[i] = modeloTabla.getColumnName(i);

        List<String[]> filas = new ArrayList<>();
        for (int f = 0; f < modeloTabla.getRowCount(); f++) {
            String[] fila = new String[modeloTabla.getColumnCount()];
            for (int c = 0; c < fila.length; c++) {
                Object valor = modeloTabla.getValueAt(f, c);
                fila[c] = valor == null ? "" : valor.toString();
            }
            filas.add(fila);
        }
        PDFReportUtil.generarReporteTabla(this, "Calendarizacion de Recursos", columnas, filas);
    }
}