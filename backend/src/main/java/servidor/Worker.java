package servidor;

import modelo.*;
import protocolo.IService;
import protocolo.Protocolo;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;

// Un Worker es un HILO que atiende a UN solo Frontend.
// Lee una peticion (un numero del Protocolo + parametros), llama al Service
// y devuelve la respuesta. Asi el Servidor puede atender a muchos a la vez.
public class Worker extends Thread {

    private final Servidor servidor;
    private final String sid;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final IService service;

    // Canal asincronico (para mandar alertas). Puede llegar un poquito despues.
    private Socket asyncSocket;
    private ObjectOutputStream asyncOut;

    private volatile Usuario usuario;   // quien inicio sesion en este Frontend (null = nadie)
    private volatile boolean activo = true;

    public Worker(Servidor servidor, String sid, Socket socket, ObjectInputStream in,
                  ObjectOutputStream out, IService service) {
        super("Worker-" + sid.substring(0, 8));
        this.servidor = servidor;
        this.sid = sid;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.service = service;
    }

    public String getSid() { return sid; }
    boolean tieneSesion() { return usuario != null; }

    synchronized void setAsync(Socket s, ObjectOutputStream o) {
        this.asyncSocket = s;
        this.asyncOut = o;
    }

    // Puede ser llamado desde OTROS hilos (el Worker de quien hizo la reserva),
    // por eso es synchronized: que no se mezclen dos alertas en el mismo stream.
    synchronized void enviarAlerta(Alerta alerta) {
        if (asyncOut == null) return;
        try {
            asyncOut.writeInt(Protocolo.DELIVER_ALERTA);
            asyncOut.writeObject(alerta);
            asyncOut.flush();
            asyncOut.reset();
        } catch (IOException e) {
            System.err.println("No se pudo enviar alerta a " + sid + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (activo) {
            try {
                int metodo = in.readInt();
                if (metodo == Protocolo.DESCONECTAR) break;
                atender(metodo);
                out.flush();
                out.reset(); // evita que ObjectOutputStream reuse objetos viejos en cache
            } catch (EOFException | SocketException e) {
                break; // el Frontend se cerro
            } catch (Exception e) {
                System.err.println("Error en worker " + sid + ": " + e);
                break;
            }
        }
        detener();
        servidor.quitarWorker(this);
    }

    void detener() {
        activo = false;
        try { socket.close(); } catch (IOException ignorado) { }
        synchronized (this) {
            try { if (asyncSocket != null) asyncSocket.close(); } catch (IOException ignorado) { }
        }
    }

    // Lee los parametros de la peticion, llama al service y escribe la respuesta.
    @SuppressWarnings("unchecked")
    private void atender(int metodo) throws IOException, ClassNotFoundException {
        try {
            Object resultado = null;
            switch (metodo) {
                case Protocolo.LOGIN -> {
                    Usuario u = service.login(leerString(), leerString());
                    usuario = u;
                    resultado = u;
                }
                case Protocolo.LOGOUT -> { service.logout(); usuario = null; }
                case Protocolo.CAMBIAR_CLAVE -> service.cambiarClave(leerString(), leerString(), leerString(), leerString());

                case Protocolo.FUNCIONARIO_LISTAR -> resultado = service.funcionarioListar();
                case Protocolo.FUNCIONARIO_BUSCAR -> resultado = service.funcionarioBuscar(leerString());
                case Protocolo.FUNCIONARIO_GUARDAR -> service.funcionarioGuardar(leerString(), leerString(), leerString(), leerString());
                case Protocolo.FUNCIONARIO_ELIMINAR -> service.funcionarioEliminar(leerString());

                case Protocolo.CATEGORIA_LISTAR -> resultado = service.categoriaListar();
                case Protocolo.CATEGORIA_BUSCAR -> resultado = service.categoriaBuscar(leerString());
                case Protocolo.CATEGORIA_GUARDAR -> service.categoriaGuardar(leerString(), leerString());
                case Protocolo.CATEGORIA_ELIMINAR -> service.categoriaEliminar(leerString());

                case Protocolo.RECURSO_LISTAR -> resultado = service.recursoListar();
                case Protocolo.RECURSO_BUSCAR -> resultado = service.recursoBuscar(leerString(), leerString());
                case Protocolo.RECURSO_GUARDAR -> service.recursoGuardar(leerString(), leerString(), leerString());
                case Protocolo.RECURSO_BORRAR -> service.recursoBorrar(leerString());

                case Protocolo.RESERVA_MIS_RESERVAS -> resultado = service.misReservas(leerString());
                case Protocolo.RESERVA_CREAR -> {
                    String funcionarioId = leerString(), actividad = leerString(), fecha = leerString(),
                            inicio = leerString(), fin = leerString();
                    List<String> categorias = (List<String>) in.readObject();
                    ResultadoReserva r = service.crearReserva(funcionarioId, actividad, fecha, inicio, fin, categorias);
                    resultado = r;
                    if (r.isExito()) servidor.notificarReserva(r.getReserva(), this); // <-- ALERTAS
                }
                case Protocolo.RESERVA_CANCELAR -> service.cancelarReserva(leerString(), leerString());

                case Protocolo.CALENDARIZACION_RECURSOS -> resultado = service.calendarizacionRecursos(leerString());
                case Protocolo.CALENDARIZACION_MATRIZ -> {
                    String fecha = leerString();
                    List<RecursoInfo> recursos = (List<RecursoInfo>) in.readObject();
                    resultado = service.calendarizacionMatriz(fecha, recursos);
                }
                case Protocolo.ACTIVIDAD_MATRIZ_SEMANA -> resultado = service.actividadMatrizSemana(leerString());
                case Protocolo.ESTADISTICA_RECURSOS -> resultado = service.estadisticaRecursos(leerString(), leerString());
                case Protocolo.ESTADISTICA_ACTIVIDADES -> resultado = service.estadisticaActividades(leerString(), leerString());

                default -> throw new IllegalArgumentException("Peticion desconocida: " + metodo);
            }
            out.writeInt(Protocolo.ERROR_NO_ERROR);
            out.writeObject(resultado);
        } catch (ValidacionException e) {
            out.writeInt(Protocolo.ERROR_VALIDACION);
            out.writeObject(e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            out.writeInt(Protocolo.ERROR_ERROR);
            out.writeObject("Error de base de datos: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            throw e; // problema de red: lo maneja run()
        } catch (Exception e) {
            out.writeInt(Protocolo.ERROR_ERROR);
            out.writeObject("Error en el servidor: " + e.getMessage());
        }
    }

    private String leerString() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }
}