package vista;

import alertas.AlertasControlador;
import alertas.AlertasModelo;
import alertas.AlertasVista;
import controlador.LoginControlador;
import proxy.ServiceProxy;
import util.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabs;
    private AlertasControlador alertasControlador;

    public MainFrame() {
        String idUsuario = Sesion.getInstancia().getUsuarioActual().getId();
        boolean esAdmin = Sesion.getInstancia().isAdministrador();

        setTitle("Sistema de Reservas - " + idUsuario + (esAdmin ? " (ADMIN)" : ""));
        setSize(1400, 700);
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

        // ---- Area de alertas (lado derecho, siempre visible, independiente de los tabs) ----
        AlertasModelo alertasModelo = new AlertasModelo();
        AlertasVista alertasVista = new AlertasVista(alertasModelo);
        alertasControlador = new AlertasControlador(alertasModelo, alertasVista);
        ServiceProxy.getInstancia().setAlertaListener(alertasControlador); // el proxy le avisara
        add(alertasVista, BorderLayout.EAST);
    }

    private JPanel construirBarraSuperior(String idUsuario, boolean esAdmin) {
        JPanel barra = new JPanel(new BorderLayout());

        JLabel lblUsuario = new JLabel("  Sesion: " + idUsuario + (esAdmin ? " (ADMIN)" : " (FUNCIONARIO)"));
        barra.add(lblUsuario, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton("Cerrar sesion");
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

        ServiceProxy.getInstancia().setAlertaListener(null); // ya no recibir alertas
        new LoginControlador().logout();                       // avisar al Backend
        Sesion.getInstancia().cerrarSesion();

        this.dispose();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}