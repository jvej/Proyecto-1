package vista;

import controlador.EstadisticaControlador;
import util.PDFReportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EstadisticaPanel extends JPanel {

    private final EstadisticaControlador controlador = new EstadisticaControlador();

    // Componentes de la columna "Recursos"
    private JTextField txtDesdeRecursos, txtHastaRecursos;
    private DefaultTableModel modeloRecursos;
    private BarChartPanel graficoRecursos;

    // Componentes de la columna "Actividades"
    private JTextField txtDesdeActividades, txtHastaActividades;
    private DefaultTableModel modeloActividades;
    private BarChartPanel graficoActividades;

    public EstadisticaPanel() {
        setLayout(new GridLayout(1, 2, 15, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construirColumnaRecursos());
        add(construirColumnaActividades());
    }

    // ---------- COLUMNA: Recursos por categoria ----------

    private JPanel construirColumnaRecursos() {
        JPanel columna = new JPanel(new BorderLayout(5, 5));
        columna.setBorder(BorderFactory.createTitledBorder("Recursos"));

        txtDesdeRecursos = new JTextField(9);
        txtHastaRecursos = new JTextField(9);
        JButton btnCargar = new JButton("✅ Cargar");
        JButton btnImprimir = new JButton("🖨 Imprimir");

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtros.add(new JLabel("Desde:"));
        filtros.add(txtDesdeRecursos);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(txtHastaRecursos);
        filtros.add(btnCargar);
        filtros.add(btnImprimir);

        modeloRecursos = new DefaultTableModel(new String[]{"Categoria", "Cantidad"}, 0);
        JTable tabla = new JTable(modeloRecursos);
        tabla.setEnabled(false);

        graficoRecursos = new BarChartPanel("Recursos Usados");

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        centro.add(new JScrollPane(tabla));
        centro.add(graficoRecursos);

        columna.add(filtros, BorderLayout.NORTH);
        columna.add(centro, BorderLayout.CENTER);

        btnCargar.addActionListener(e -> cargarRecursos());
        btnImprimir.addActionListener(e -> imprimirTabla(modeloRecursos, "Estadisticas de Recursos por Categoria"));

        return columna;
    }

    private void cargarRecursos() {
        try {
            Map<String, Integer> conteo = controlador.estadisticasRecursosPorCategoria(
                    txtDesdeRecursos.getText(), txtHastaRecursos.getText());

            modeloRecursos.setRowCount(0);
            for (Map.Entry<String, Integer> entrada : conteo.entrySet()) {
                modeloRecursos.addRow(new Object[]{entrada.getKey(), entrada.getValue()});
            }
            graficoRecursos.setDatos(conteo);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- COLUMNA: Actividades por semana ----------

    private JPanel construirColumnaActividades() {
        JPanel columna = new JPanel(new BorderLayout(5, 5));
        columna.setBorder(BorderFactory.createTitledBorder("Actividades"));

        txtDesdeActividades = new JTextField(9);
        txtHastaActividades = new JTextField(9);
        JButton btnCargar = new JButton("✅ Cargar");
        JButton btnImprimir = new JButton("🖨 Imprimir");

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtros.add(new JLabel("Desde:"));
        filtros.add(txtDesdeActividades);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(txtHastaActividades);
        filtros.add(btnCargar);
        filtros.add(btnImprimir);

        modeloActividades = new DefaultTableModel(new String[]{"Semana (lunes)", "Cantidad"}, 0);
        JTable tabla = new JTable(modeloActividades);
        tabla.setEnabled(false);

        graficoActividades = new BarChartPanel("Actividades Realizadas");

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        centro.add(new JScrollPane(tabla));
        centro.add(graficoActividades);

        columna.add(filtros, BorderLayout.NORTH);
        columna.add(centro, BorderLayout.CENTER);

        btnCargar.addActionListener(e -> cargarActividades());
        btnImprimir.addActionListener(e -> imprimirTabla(modeloActividades, "Estadisticas de Actividades por Semana"));

        return columna;
    }

    private void cargarActividades() {
        try {
            Map<LocalDate, Integer> conteo = controlador.estadisticasActividadesPorSemana(
                    txtDesdeActividades.getText(), txtHastaActividades.getText());

            modeloActividades.setRowCount(0);
            // BarChartPanel trabaja con Map<String,Integer>, asi que convertimos la clave LocalDate a texto.
            java.util.Map<String, Integer> paraGrafico = new java.util.LinkedHashMap<>();
            for (Map.Entry<LocalDate, Integer> entrada : conteo.entrySet()) {
                String semana = entrada.getKey().toString();
                modeloActividades.addRow(new Object[]{semana, entrada.getValue()});
                paraGrafico.put(semana, entrada.getValue());
            }
            graficoActividades.setDatos(paraGrafico);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Reporte PDF (compartido por ambas columnas) ----------

    private void imprimirTabla(DefaultTableModel modelo, String titulo) {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Primero cargue datos para generar el reporte.");
            return;
        }
        String[] columnas = new String[modelo.getColumnCount()];
        for (int i = 0; i < columnas.length; i++) columnas[i] = modelo.getColumnName(i);

        List<String[]> filas = new ArrayList<>();
        for (int f = 0; f < modelo.getRowCount(); f++) {
            String[] fila = new String[modelo.getColumnCount()];
            for (int c = 0; c < fila.length; c++) {
                Object valor = modelo.getValueAt(f, c);
                fila[c] = valor == null ? "" : valor.toString();
            }
            filas.add(fila);
        }
        PDFReportUtil.generarReporteTabla(this, titulo, columnas, filas);
    }
}
