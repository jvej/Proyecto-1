package vista;

import util.Sesion;

import javax.swing.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabs;

    public MainFrame() {
        String idUsuario = Sesion.getInstancia().getUsuarioActual().getId();
        boolean esAdmin = Sesion.getInstancia().isAdministrador();

        setTitle("Sistema de Reservas - " + idUsuario + (esAdmin ? " (ADMIN)" : ""));
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        // Tab temporal para probar el login; se reemplaza cuando existan los paneles reales.
        JPanel bienvenida = new JPanel();
        bienvenida.add(new JLabel("Bienvenido, " + idUsuario + ". Login funcionando correctamente."));
        tabs.addTab("Inicio", bienvenida);

        if (esAdmin) {
            tabs.addTab("Funcionarios", new FuncionarioPanel());
            tabs.addTab("Categorias", new CategoriaPanel());
        }

        // TODO (Coso): tabs.addTab("Calendarizacion", new CalendarizacionPanel());
        // TODO (Axel): tabs.addTab("Recursos", new RecursoPanel());          -- solo si esAdmin
        // TODO (Axel): tabs.addTab("Reservas", new ReservaPanel());          -- solo si !esAdmin
        // TODO (Mariano): tabs.addTab("Actividades", new ActividadPanel());
        // TODO (Mariano): tabs.addTab("Estadisticas", new EstadisticaPanel());

        add(tabs);
    }
}