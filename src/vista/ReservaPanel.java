package vista;
import controlador.ReservaControlador;
import modelo.Categoria;
import modelo.Reserva;
import util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ReservaPanel extends JPanel{
    private final ReservaControlador controlador = new ReservaControlador();
    private final List<Categoria> categorias = new CategoriaService().listar();
    private final List<JCheckBox> checksCategorias = new ArrayList<>();

    private JTextField txtFrase;
    private JTextField txtActividad;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public ReservaPanel() {
        setLayout(null);

        //Frase para IA
        JLabel lblFrase = new JLabel("Frase:");
        lblFrase.setBounds(20, 15, 60, 25);
        add(lblFrase);

        txtFrase = new JTextField();
        txtFrase.setBounds(80, 15, 500, 25);
        add(txtFrase);

        JButton btnExtraer = new JButton("Extraer (IA)");
        btnExtraer.setBounds(590, 15, 130, 25);
        add(btnExtraer);

        //Formulario
        JLabel lblActividad = new JLabel("Actividad:");
        lblActividad.setBounds(20, 55, 100, 25);
        add(lblActividad);

        txtActividad = new JTextField();
        txtActividad.setBounds(120, 55, 300, 25);
        add(txtActividad);

        JLabel lblFecha = new JLabel("Fecha (aaaa-mm-dd):");
        lblFecha.setBounds(20, 90, 150, 25);
        add(lblFecha);

        txtFecha = new JTextField();
        txtFecha.setBounds(170, 90, 120, 25);
        add(txtFecha);

        JLabel lblHoraInicio = new JLabel("Hora inicio (hh:mm):");
        lblHoraInicio.setBounds(300, 90, 150, 25);
        add(lblHoraInicio);

        txtHoraInicio = new JTextField();
        txtHoraInicio.setBounds(450, 90, 80, 25);
        add(txtHoraInicio);

        JLabel lblHoraFin = new JLabel("Hora fin (hh:mm):");
        lblHoraFin.setBounds(540, 90, 130, 25);
        add(lblHoraFin);

        txtHoraFin = new JTextField();
        txtHoraFin.setBounds(670, 90, 80, 25);
        add(txtHoraFin);

        //Categorías (checkboxes, uno por categoría real)
        JLabel lblCategorias = new JLabel("Categorías requeridas:");
        lblCategorias.setBounds(20, 125, 200, 20);
        add(lblCategorias);

        int y = 150;
        for (Categoria c : categorias) {
            JCheckBox chk = new JCheckBox(c.getDescripcion());
            chk.putClientProperty("categoriaId", c.getId());
            chk.setBounds(20, y, 300, 22);
            add(chk);
            checksCategorias.add(chk);
            y += 24;
        }

        //Botones
        JButton btnReservar = new JButton("Reservar");
        btnReservar.setBounds(20, y + 10, 110, 28);
        add(btnReservar);

        JButton btnCancelar = new JButton("Cancelar reserva seleccionada");
        btnCancelar.setBounds(140, y + 10, 220, 28);
        add(btnCancelar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(370, y + 10, 100, 28);
        add(btnLimpiar);

        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.setBounds(480, y + 10, 100, 28);
        add(btnImprimir);

        //Tabla "Mis reservas"
        JLabel lblListado = new JLabel("Mis reservas:");
        lblListado.setBounds(20, y + 45, 150, 20);
        add(lblListado);

        modeloTabla = new DefaultTableModel(
                new Object[]{"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, y + 70, 750, 200);
        add(scroll);

        //Eventos
        btnReservar.addActionListener(e ->reservar());
        btnCancelar.addActionListener(e ->cancelar());
        btnLimpiar.addActionListener(e ->limpiar());
        btnImprimir.addActionListener(e ->imprimir());
        btnExtraer.addActionListener(e ->extraerConIA());

        cargarTabla();
    }

    //Acciones

    private void reservar() {
        try {
            String funcionarioId = Sesion.getInstancia().getUsuarioActual().getId();
            String actividad = txtActividad.getText();
            LocalDate fecha = parsearFecha(txtFecha.getText());
            LocalTime horaInicio = parsearHora(txtHoraInicio.getText());
            LocalTime horaFin = parsearHora(txtHoraFin.getText());
            List<String> categoriasSeleccionadas = obtenerCategoriasMarcadas();

            ResultadoReserva resultado = controlador.crearReserva(
                    funcionarioId, actividad, fecha, horaInicio, horaFin, categoriasSeleccionadas);

            if (resultado.isExito()) {
                JOptionPane.showMessageDialog(this,
                        "Reserva creada correctamente: " + resultado.getReserva().getId());
                limpiar();
                cargarTabla();
            } else {
                String faltantes = String.join(", ", nombresDeCategorias(resultado.getCategoriasNoDisponibles()));
                JOptionPane.showMessageDialog(this,
                        "No se pudo reservar. Sin disponibilidad para: " + faltantes,
                        "Sin disponibilidad", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla.");
            return;
        }
        try {
            String id = (String) modeloTabla.getValueAt(fila, 0);
            String funcionarioId = Sesion.getInstancia().getUsuarioActual().getId();
            controlador.cancelarReserva(id, funcionarioId);
            JOptionPane.showMessageDialog(this, "Reserva cancelada.");
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtFrase.setText("");
        txtActividad.setText("");
        txtFecha.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        for (JCheckBox chk : checksCategorias) chk.setSelected(false);
        tabla.clearSelection();
    }

    private void imprimir() {
        String[] columnas = {"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"};
        List<String[]> filas = new ArrayList<>();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String[] fila = new String[columnas.length];
            for (int c = 0; c < columnas.length; c++) {
                fila[c] = String.valueOf(modeloTabla.getValueAt(i, c));
            }
            filas.add(fila);
        }
        PDFReportUtil.generarReporteTabla(this, "Mis Reservas", columnas, filas);
    }

    private void extraerConIA() {
        String frase = txtFrase.getText().trim();
        if (frase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba una frase describiendo la reserva.");
            return;
        }
        try {
            List<String> descripcionesCategorias = new ArrayList<>();
            for (Categoria c : categorias) descripcionesCategorias.add(c.getDescripcion());

            DatosExtraidos datos = new IAExtractorService().extraer(frase, descripcionesCategorias);

            txtActividad.setText(datos.getActividad());
            txtFecha.setText(datos.getFecha());
            txtHoraInicio.setText(datos.getHoraInicio());
            txtHoraFin.setText(datos.getHoraFin());

            for (JCheckBox chk : checksCategorias) {
                chk.setSelected(datos.getCategoriasDescripciones().contains(chk.getText()));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo extraer con IA: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Helpers
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String funcionarioId = Sesion.getInstancia().getUsuarioActual().getId();
        for (Reserva r : controlador.misReservas(funcionarioId)) {
            String horario = r.getHoraInicio() + " - " + r.getHoraFin();
            String recursos = String.join(", ", r.getRecursosIds());
            modeloTabla.addRow(new Object[]{
                    r.getId(), r.getActividad(), r.getFecha().toString(), horario, recursos, r.getEstado()
            });
        }
    }

    private List<String> obtenerCategoriasMarcadas() {
        List<String> seleccionadas = new ArrayList<>();
        for (JCheckBox chk : checksCategorias) {
            if (chk.isSelected()) {
                seleccionadas.add((String) chk.getClientProperty("categoriaId"));
            }
        }
        return seleccionadas;
    }

    private List<String> nombresDeCategorias(List<String> ids) {
        List<String> nombres = new ArrayList<>();
        for (String id : ids) {
            for (Categoria c : categorias) {
                if (c.getId().equalsIgnoreCase(id)) {
                    nombres.add(c.getDescripcion());
                    break;
                }
            }
        }
        return nombres;
    }

    private LocalDate parsearFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha inválida. Use el formato aaaa-mm-dd.");
        }
    }

    private LocalTime parsearHora(String texto) {
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Hora inválida. Use el formato hh:mm.");
        }
    }
}
