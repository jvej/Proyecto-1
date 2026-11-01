package alertas;

import modelo.Alerta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// VISTA (MVC) del area de alertas. Solo dibuja y captura clicks;
// las decisiones las toma el AlertasControlador.
public class AlertasVista extends JPanel {

    private final JTable tabla;
    private AlertasControlador controlador;

    public AlertasVista(AlertasModelo modelo) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 0));
        setBorder(BorderFactory.createTitledBorder("Alertas"));

        tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(AlertasModelo.COL_ACTIVIDAD).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(AlertasModelo.COL_VER).setMaxWidth(40);
        tabla.getColumnModel().getColumn(AlertasModelo.COL_DESCARTAR).setMaxWidth(45);

        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int col = tabla.columnAtPoint(e.getPoint());
                if (fila < 0 || controlador == null) return;
                if (col == AlertasModelo.COL_VER) controlador.ver(fila);
                else if (col == AlertasModelo.COL_DESCARTAR) controlador.descartar(fila);
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public void setControlador(AlertasControlador controlador) { this.controlador = controlador; }

    // Ventana emergente con el detalle de la reserva
    public void mostrarDetalle(Alerta a) {
        Window padre = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(padre, a.getActividad(), Dialog.ModalityType.APPLICATION_MODAL);

        JPanel datos = new JPanel(new GridBagLayout());
        datos.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(3, 3, 3, 10);

        String[][] filas = {
                {"Encargado:", a.getEncargado()},
                {"Fecha:", String.valueOf(a.getFecha())},
                {"Desde:", String.valueOf(a.getHoraInicio())},
                {"Hasta:", String.valueOf(a.getHoraFin())},
        };
        for (int i = 0; i < filas.length; i++) {
            g.gridy = i; g.gridx = 0;
            datos.add(new JLabel(filas[i][0]), g);
            g.gridx = 1;
            datos.add(new JLabel(filas[i][1]), g);
        }
        g.gridy = filas.length; g.gridx = 0;
        datos.add(new JLabel("Recursos:"), g);
        g.gridx = 1;
        JList<String> lista = new JList<>(a.getRecursos().toArray(new String[0]));
        lista.setEnabled(false);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(220, 90));
        datos.add(scroll, g);

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> dialogo.dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(ok);

        dialogo.setLayout(new BorderLayout());
        dialogo.add(datos, BorderLayout.CENTER);
        dialogo.add(botones, BorderLayout.SOUTH);
        dialogo.getRootPane().setDefaultButton(ok);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    public boolean confirmarDescarte(Alerta a) {
        int r = JOptionPane.showConfirmDialog(this, a.getActividad(), "¿Descarta?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    JTable getTabla() { return tabla; }
}