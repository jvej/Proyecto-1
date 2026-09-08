package vista;

import controlador.ActividadControlador;
import controlador.ActividadControlador.Celda;
import util.PDFReportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActividadPanel extends JPanel {

    private final ActividadControlador controlador = new ActividadControlador();

    private JTextField txtFecha;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private static final DateTimeFormatter FORMATO_CORTO = DateTimeFormatter.ofPattern("EEE dd/MM");

    public ActividadPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirPanelFiltros(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);
    }

    private JPanel construirPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Semana"));

        panel.add(new JLabel("Fecha de referencia (yyyy-MM-dd):"));
        txtFecha = new JTextField(10);
        panel.add(txtFecha);

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
            Map<LocalDate, Map<Integer, Celda>> matriz = controlador.obtenerMatrizSemana(txtFecha.getText());

            // Dias ordenados de lunes a domingo (el Map de Java no garantiza orden, asi que los orden aparte).
            List<LocalDate> dias = new ArrayList<>(matriz.keySet());
            dias.sort(LocalDate::compareTo);

            // Columnas: "Hora" + una por cada dia de la semana
            String[] columnas = new String[dias.size() + 1];
            columnas[0] = "Hora";
            for (int i = 0; i < dias.size(); i++) {
                columnas[i + 1] = dias.get(i).format(FORMATO_CORTO);
            }
            modeloTabla = new DefaultTableModel(columnas, 0);

            for (int hora = ActividadControlador.HORA_DESDE; hora <= ActividadControlador.HORA_HASTA; hora++) {
                Object[] fila = new Object[dias.size() + 1];
                fila[0] = String.format("%02d:00", hora);
                for (int i = 0; i < dias.size(); i++) {
                    Celda celda = matriz.get(dias.get(i)).get(hora);
                    fila[i + 1] = celda == null ? "" : celda.actividad + " (" + celda.idFuncionario + ")";
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
            JOptionPane.showMessageDialog(this, "Primero cargue una semana.");
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
        PDFReportUtil.generarReporteTabla(this, "Programacion de Actividades", columnas, filas);
    }
}
