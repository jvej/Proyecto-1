package App;

import protocolo.Protocolo;
import proxy.ServiceProxy;
import vista.LoginFrame;

import javax.swing.*;

// Punto de entrada del FRONTEND.
// Uso:  java -jar frontend.jar [servidor] [puerto]
//       ej: java -jar frontend.jar 192.168.1.50 1234
public class Main {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : Protocolo.SERVIDOR;
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : Protocolo.PUERTO;

        // Cualquier error no atrapado se muestra en un mensaje (en vez de solo en consola).
        Thread.setDefaultUncaughtExceptionHandler((hilo, ex) -> SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)));

        // Al iniciar, el Frontend se conecta con el Backend.
        try {
            ServiceProxy.getInstancia().conectar(host, puerto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con el Backend en " + host + ":" + puerto
                            + "\n¿Ya está corriendo el servidor?\n\n" + e.getMessage(),
                    "Sin conexión", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ServiceProxy.getInstancia().desconectar()));

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}