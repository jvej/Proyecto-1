package servidor;

import modelo.Alerta;
import modelo.ResultadoReserva;
import modelo.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocolo.Protocolo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test de INTEGRACION del servidor de sockets (hilos + protocolo + alertas).
// Levanta un Servidor real en un puerto libre, con un Service falso (sin MySQL),
// y conecta dos "Frontend" de prueba.
class ServidorIT {

    private Servidor servidor;

    // Un cliente minimo que habla el protocolo, como lo hace el ServiceProxy.
    static class Cliente {
        Socket s, as;
        ObjectOutputStream out;
        ObjectInputStream in, ain;

        Cliente(int puerto) throws Exception {
            s = new Socket("localhost", puerto);
            out = new ObjectOutputStream(s.getOutputStream()); out.flush();
            in = new ObjectInputStream(s.getInputStream());
            out.writeInt(Protocolo.SYNC); out.flush();
            String sid = (String) in.readObject();

            as = new Socket("localhost", puerto);
            ObjectOutputStream aout = new ObjectOutputStream(as.getOutputStream()); aout.flush();
            ain = new ObjectInputStream(as.getInputStream());
            aout.writeInt(Protocolo.ASYNC); aout.writeObject(sid); aout.flush();
            as.setSoTimeout(3000);
        }

        Object[] pedir(int metodo, Object... params) throws Exception {
            out.writeInt(metodo);
            for (Object p : params) out.writeObject(p);
            out.flush();
            int codigo = in.readInt();
            return new Object[]{codigo, in.readObject()};
        }

        void cerrar() throws Exception { s.close(); as.close(); }
    }

    @BeforeEach
    void levantar() throws Exception {
        servidor = new Servidor(0, new ServiceFalso(), r -> new Alerta(r.getId(), "Juan Mora", r.getActividad(),
                r.getFecha(), r.getHoraInicio(), r.getHoraFin(), r.getRecursosIds()));
        servidor.iniciarEnHilo();
    }

    @AfterEach
    void bajar() { servidor.detener(); }

    @Test
    void loginCorrectoDevuelveUsuario() throws Exception {
        Cliente c = new Cliente(servidor.getPuerto());
        Object[] r = c.pedir(Protocolo.LOGIN, "111", "1234");
        assertEquals(Protocolo.ERROR_NO_ERROR, r[0]);
        assertEquals("111", ((Usuario) r[1]).getId());
        c.cerrar();
    }

    @Test
    void errorDeValidacionViajaComoMensaje() throws Exception {
        Cliente c = new Cliente(servidor.getPuerto());
        Object[] r = c.pedir(Protocolo.CATEGORIA_GUARDAR, "", "");
        assertEquals(Protocolo.ERROR_VALIDACION, r[0]);
        assertEquals("La descripción es obligatoria.", r[1]);
        c.cerrar();
    }

    @Test
    void alCrearReservaLosDemasRecibenAlertaYElCreadorNo() throws Exception {
        Cliente a = new Cliente(servidor.getPuerto());
        Cliente b = new Cliente(servidor.getPuerto());
        Thread.sleep(200); // dar tiempo a que el servidor enganche los canales async
        a.pedir(Protocolo.LOGIN, "111", "1234");
        b.pedir(Protocolo.LOGIN, "222", "1234");

        Object[] r = a.pedir(Protocolo.RESERVA_CREAR, "111", "Reunion clientes", "2026-12-01", "08:00", "10:00",
                new ArrayList<>(List.of("CAT-000001")));
        assertEquals(Protocolo.ERROR_NO_ERROR, r[0]);
        assertTrue(((ResultadoReserva) r[1]).isExito());

        // B recibe la alerta por su canal asincronico
        assertEquals(Protocolo.DELIVER_ALERTA, b.ain.readInt());
        Alerta alerta = (Alerta) b.ain.readObject();
        assertEquals("Juan Mora - Reunion clientes", alerta.getTitulo());

        // A (el creador) NO recibe nada
        a.as.setSoTimeout(500);
        assertThrows(java.net.SocketTimeoutException.class, () -> a.ain.readInt());

        a.cerrar();
        b.cerrar();
    }
}