package proxy;

import modelo.Alerta;
import modelo.Usuario;
import modelo.Rol;
import modelo.ValidacionException;
import org.junit.jupiter.api.Test;
import protocolo.Protocolo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

// Test de INTEGRACION del Proxy: se levanta un "Backend de mentira" con un
// ServerSocket real y se verifica que el Proxy envia/recibe bien por la red
// y que las alertas asincronicas llegan al listener.
class ServiceProxyIT {

    @Test
    void proxyHablaConElServidorYRecibeAlertas() throws Exception {
        try (ServerSocket ss = new ServerSocket(0)) {
            Thread falso = new Thread(() -> servidorFalso(ss));
            falso.setDaemon(true);
            falso.start();

            BlockingQueue<Alerta> recibidas = new ArrayBlockingQueue<>(1);
            ServiceProxy proxy = new ServiceProxy();
            proxy.setAlertaListener(recibidas::add);
            proxy.conectar("localhost", ss.getLocalPort());

            Usuario u = proxy.login("111", "1234");
            assertEquals("111", u.getId());

            ValidacionException ex = assertThrows(ValidacionException.class, () -> proxy.categoriaGuardar("", ""));
            assertEquals("La descripción es obligatoria.", ex.getMessage());

            Alerta a = recibidas.poll(3, TimeUnit.SECONDS);
            assertNotNull(a);
            assertEquals("Ana Lopez - Coordinacion viaje", a.getTitulo());

            proxy.desconectar();
        }
    }

    private void servidorFalso(ServerSocket ss) {
        try {
            Socket sync = ss.accept();
            ObjectOutputStream out = new ObjectOutputStream(sync.getOutputStream()); out.flush();
            ObjectInputStream in = new ObjectInputStream(sync.getInputStream());
            in.readInt();                 // SYNC
            out.writeObject("sid-test"); out.flush();

            Socket async = ss.accept();
            ObjectOutputStream aout = new ObjectOutputStream(async.getOutputStream()); aout.flush();
            ObjectInputStream ain = new ObjectInputStream(async.getInputStream());
            ain.readInt(); ain.readObject(); // ASYNC + sid

            // 1) LOGIN
            assertEquals(Protocolo.LOGIN, in.readInt());
            in.readObject(); in.readObject();
            out.writeInt(Protocolo.ERROR_NO_ERROR);
            out.writeObject(new Usuario("111", "", Rol.FUNCIONARIO));
            out.flush();

            // 2) CATEGORIA_GUARDAR -> error de validacion
            assertEquals(Protocolo.CATEGORIA_GUARDAR, in.readInt());
            in.readObject(); in.readObject();
            out.writeInt(Protocolo.ERROR_VALIDACION);
            out.writeObject("La descripción es obligatoria.");
            out.flush();

            // 3) Notificacion asincronica
            aout.writeInt(Protocolo.DELIVER_ALERTA);
            aout.writeObject(new Alerta("RES-7", "Ana Lopez", "Coordinacion viaje", LocalDate.now(),
                    LocalTime.of(14, 0), LocalTime.of(15, 0), List.of("Sala 1")));
            aout.flush();

            in.readInt(); // DESCONECTAR
        } catch (Exception ignorado) { }
    }
}