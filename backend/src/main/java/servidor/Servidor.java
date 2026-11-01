package servidor;

import logica.Service;
import modelo.Alerta;
import protocolo.IService;
import protocolo.Protocolo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

// El Servidor "escucha" en un puerto. Cada vez que un Frontend se conecta:
//   1) Si es una conexion SYNC: le asigna un id de sesion (sid), crea un Worker
//      (un HILO solo para ese Frontend) y le devuelve el sid.
//   2) Si es una conexion ASYNC: el Frontend manda su sid y el Servidor le
//      "engancha" ese segundo socket a su Worker. Por ahi se mandan las alertas.
public class Servidor {

    private final ServerSocket serverSocket;
    private final IService service;
    private final Function<modelo.Reserva, Alerta> fabricaAlertas;
    private final List<Worker> workers = new CopyOnWriteArrayList<>(); // lista segura entre hilos
    private volatile boolean corriendo = true;

    public Servidor(int puerto) throws IOException {
        this(puerto, Service.getInstancia(), reserva -> {
            try { return Service.getInstancia().crearAlerta(reserva); }
            catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    // Constructor que permite inyectar otro IService (lo usan los tests de integracion).
    public Servidor(int puerto, IService service, Function<modelo.Reserva, Alerta> fabricaAlertas) throws IOException {
        this.serverSocket = new ServerSocket(puerto);
        this.service = service;
        this.fabricaAlertas = fabricaAlertas;
    }

    public int getPuerto() { return serverSocket.getLocalPort(); }

    // Ciclo principal: aceptar conexiones para siempre.
    public void run() {
        System.out.println("Backend escuchando en el puerto " + getPuerto() + " ...");
        while (corriendo) {
            try {
                Socket socket = serverSocket.accept();
                atenderConexion(socket);
            } catch (IOException e) {
                if (corriendo) System.err.println("Error aceptando conexion: " + e.getMessage());
            }
        }
    }

    // Arranca el servidor en un hilo aparte (util para tests).
    public Thread iniciarEnHilo() {
        Thread t = new Thread(this::run, "Servidor");
        t.setDaemon(true);
        t.start();
        return t;
    }

    public void detener() {
        corriendo = false;
        try { serverSocket.close(); } catch (IOException ignorado) { }
        for (Worker w : workers) w.detener();
    }

    private void atenderConexion(Socket socket) {
        try {
            // OJO: crear primero el OutputStream y hacer flush, luego el InputStream
            // (si ambos lados crean primero el InputStream, se quedan esperando para siempre).
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            int tipo = in.readInt();
            if (tipo == Protocolo.SYNC) {
                String sid = UUID.randomUUID().toString();
                out.writeObject(sid);
                out.flush();
                Worker w = new Worker(this, sid, socket, in, out, service);
                workers.add(w);
                w.start();
                System.out.println("Frontend conectado. sid=" + sid + " (" + socket.getInetAddress() + ")");
            } else if (tipo == Protocolo.ASYNC) {
                String sid = (String) in.readObject();
                Worker w = buscarWorker(sid);
                if (w == null) {
                    socket.close();
                    return;
                }
                w.setAsync(socket, out);
                System.out.println("Canal de alertas listo para sid=" + sid);
            } else {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Conexion rechazada: " + e.getMessage());
            try { socket.close(); } catch (IOException ignorado) { }
        }
    }

    private Worker buscarWorker(String sid) {
        for (Worker w : workers) if (w.getSid().equals(sid)) return w;
        return null;
    }

    void quitarWorker(Worker w) {
        workers.remove(w);
        System.out.println("Frontend desconectado. sid=" + w.getSid());
    }

    // Se llama cuando un usuario crea una reserva: avisa a TODOS los demas
    // Frontend que tengan sesion iniciada (menos al que la creo).
    void notificarReserva(modelo.Reserva reserva, Worker origen) {
        Alerta alerta;
        try {
            alerta = fabricaAlertas.apply(reserva);
        } catch (Exception e) {
            System.err.println("No se pudo armar la alerta: " + e.getMessage());
            return;
        }
        for (Worker w : workers) {
            if (w != origen && w.tieneSesion()) w.enviarAlerta(alerta);
        }
    }

    int cantidadConectados() { return workers.size(); }
}