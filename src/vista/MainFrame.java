package vista;

import util.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabs;

    public MainFrame() {
        String idUsuario = Sesion.getInstancia().getUsuarioActual().getId();
        boolean esAdmin = Sesion.getInstancia().isAdministrador();

        setTitle("Sistema de Reservas - " + idUsuario + (esAdmin ? " (ADMIN)" : ""));
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(construirBarraSuperior(idUsuario, esAdmin), BorderLayout.NORTH);

        tabs = new JTabbedPane();

        if (esAdmin) {
            tabs.addTab("Funcionarios", new FuncionarioPanel());
            tabs.addTab("Categorias", new CategoriaPanel());
        }

        tabs.addTab("Calendarizacion", new CalendarizacionPanel());

        if (esAdmin) {
            tabs.addTab("Recursos", new RecursoPanel());
        } else {
            tabs.addTab("Reservas", new ReservaPanel());
        }
        tabs.addTab("Actividades", new ActividadPanel());
        tabs.addTab("Estadisticas", new EstadisticaPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel construirBarraSuperior(String idUsuario, boolean esAdmin) {
        JPanel barra = new JPanel(new BorderLayout());

        JLabel lblUsuario = new JLabel("  Sesion: " + idUsuario + (esAdmin ? " (ADMIN)" : " (FUNCIONARIO)"));
        barra.add(lblUsuario, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton("Log out");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        derecha.add(btnCerrarSesion);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Esta seguro de que desea cerrar sesion?",
                "Cerrar sesion",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        Sesion.getInstancia().cerrarSesion();

        this.dispose();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}