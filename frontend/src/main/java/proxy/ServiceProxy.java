package proxy;

import modelo.*;
import protocolo.IService;
import protocolo.Protocolo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// PATRON PROXY:
// Para el resto del Frontend, este objeto "ES" el Service (implementa IService).
// Pero en realidad no hace el trabajo: empaqueta cada llamada, la manda por el
// socket al Backend, espera la respuesta y la devuelve como si nada.
//
// Ademas abre un SEGUNDO socket (asincronico) y un hilo que se queda escuchando
// las alertas que el Backend manda cuando otros usuarios crean reservas.
public class ServiceProxy implements IService {

    private static ServiceProxy instancia;

    public static synchronized ServiceProxy getInstancia() {
        if (instancia == null) instancia = new ServiceProxy();
        return instancia;
    }

    // Publico solo para poder crear instancias independientes en los tests.
    public ServiceProxy() { }

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String sid;

    private Socket asyncSocket;
    private Thread hiloAlertas;
    private volatile boolean escuchando;
    private volatile AlertaListener alertaListener; // a quien avisarle cuando llega una alerta

    // Quien quiera recibir alertas implementa esta interfaz (el AlertasControlador).
    public interface AlertaListener {
        void alertaRecibida(Alerta alerta);
    }

    public void setAlertaListener(AlertaListener listener) { this.alertaListener = listener; }

    // ===================== Conexion =====================

    public synchronized void conectar(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        out.writeInt(Protocolo.SYNC);
        out.flush();
        try {
            sid = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Respuesta inesperada del servidor", e);
        }
        iniciarCanalAsincronico(host, puerto);
    }

    public boolean estaConectado() { return socket != null && socket.isConnected() && !socket.isClosed(); }

    public synchronized void desconectar() {
        escuchando = false;
        try {
            if (out != null) { out.writeInt(Protocolo.DESCONECTAR); out.flush(); }
        } catch (IOException ignorado) { }
        try { if (socket != null) socket.close(); } catch (IOException ignorado) { }
        try { if (asyncSocket != null) asyncSocket.close(); } catch (IOException ignorado) { }
    }

    private void iniciarCanalAsincronico(String host, int puerto) throws IOException {
        asyncSocket = new Socket(host, puerto);
        ObjectOutputStream aOut = new ObjectOutputStream(asyncSocket.getOutputStream());
        aOut.flush();
        ObjectInputStream aIn = new ObjectInputStream(asyncSocket.getInputStream());
        aOut.writeInt(Protocolo.ASYNC);
        aOut.writeObject(sid);
        aOut.flush();

        escuchando = true;
        hiloAlertas = new Thread(() -> escucharAlertas(aIn), "Escucha-Alertas");
        hiloAlertas.setDaemon(true); // no impide que la aplicacion se cierre
        hiloAlertas.start();
    }

    // Este metodo corre en su PROPIO HILO: se bloquea esperando alertas del Backend.
    private void escucharAlertas(ObjectInputStream aIn) {
        while (escuchando) {
            try {
                int tipo = aIn.readInt();
                if (tipo == Protocolo.DELIVER_ALERTA) {
                    Alerta alerta = (Alerta) aIn.readObject();
                    AlertaListener l = alertaListener;
                    if (l != null) l.alertaRecibida(alerta);
                }
            } catch (Exception e) {
                escuchando = false; // se cerro la conexion
            }
        }
    }

    // ===================== Envio de peticiones =====================

    // Manda: el codigo de la peticion + los parametros. Recibe: codigo de error + resultado.
    // synchronized: si dos partes del Frontend piden algo a la vez, que no se mezclen.
    private synchronized Object enviar(int metodo, Object... parametros) throws Exception {
        if (!estaConectado()) throw new ValidacionException("No hay conexión con el servidor.");
        try {
            out.writeInt(metodo);
            for (Object p : parametros) out.writeObject(p);
            out.flush();
            out.reset();

            int codigo = in.readInt();
            Object respuesta = in.readObject();
            if (codigo == Protocolo.ERROR_NO_ERROR) return respuesta;
            throw new ValidacionException((String) respuesta); // error de validacion o del servidor
        } catch (IOException e) {
            throw new ValidacionException("Se perdió la conexión con el servidor: " + e.getMessage());
        }
    }

    // ===================== IService =====================

    @Override public Usuario login(String id, String clave) throws Exception {
        return (Usuario) enviar(Protocolo.LOGIN, id, clave);
    }
    @Override public void logout() throws Exception { enviar(Protocolo.LOGOUT); }
    @Override public void cambiarClave(String id, String actual, String nueva, String conf) throws Exception {
        enviar(Protocolo.CAMBIAR_CLAVE, id, actual, nueva, conf);
    }

    @SuppressWarnings("unchecked")
    @Override public List<Funcionario> funcionarioListar() throws Exception {
        return (List<Funcionario>) enviar(Protocolo.FUNCIONARIO_LISTAR);
    }
    @SuppressWarnings("unchecked")
    @Override public List<Funcionario> funcionarioBuscar(String texto) throws Exception {
        return (List<Funcionario>) enviar(Protocolo.FUNCIONARIO_BUSCAR, texto);
    }
    @Override public void funcionarioGuardar(String id, String nombre, String telefono, String clave) throws Exception {
        enviar(Protocolo.FUNCIONARIO_GUARDAR, id, nombre, telefono, clave);
    }
    @Override public void funcionarioEliminar(String id) throws Exception { enviar(Protocolo.FUNCIONARIO_ELIMINAR, id); }

    @SuppressWarnings("unchecked")
    @Override public List<Categoria> categoriaListar() throws Exception {
        return (List<Categoria>) enviar(Protocolo.CATEGORIA_LISTAR);
    }
    @SuppressWarnings("unchecked")
    @Override public List<Categoria> categoriaBuscar(String texto) throws Exception {
        return (List<Categoria>) enviar(Protocolo.CATEGORIA_BUSCAR, texto);
    }
    @Override public void categoriaGuardar(String id, String descripcion) throws Exception {
        enviar(Protocolo.CATEGORIA_GUARDAR, id, descripcion);
    }
    @Override public void categoriaEliminar(String id) throws Exception { enviar(Protocolo.CATEGORIA_ELIMINAR, id); }

    @SuppressWarnings("unchecked")
    @Override public List<Recurso> recursoListar() throws Exception {
        return (List<Recurso>) enviar(Protocolo.RECURSO_LISTAR);
    }
    @SuppressWarnings("unchecked")
    @Override public List<Recurso> recursoBuscar(String categoriaId, String descripcion) throws Exception {
        return (List<Recurso>) enviar(Protocolo.RECURSO_BUSCAR, categoriaId, descripcion);
    }
    @Override public void recursoGuardar(String id, String categoriaId, String descripcion) throws Exception {
        enviar(Protocolo.RECURSO_GUARDAR, id, categoriaId, descripcion);
    }
    @Override public void recursoBorrar(String id) throws Exception { enviar(Protocolo.RECURSO_BORRAR, id); }

    @SuppressWarnings("unchecked")
    @Override public List<Reserva> misReservas(String funcionarioId) throws Exception {
        return (List<Reserva>) enviar(Protocolo.RESERVA_MIS_RESERVAS, funcionarioId);
    }
    @Override public ResultadoReserva crearReserva(String funcionarioId, String actividad, String fecha, String horaInicio,
                                                   String horaFin, List<String> categoriasIds) throws Exception {
        return (ResultadoReserva) enviar(Protocolo.RESERVA_CREAR, funcionarioId, actividad, fecha, horaInicio, horaFin,
                new ArrayList<>(categoriasIds));
    }
    @Override public void cancelarReserva(String id, String funcionarioId) throws Exception {
        enviar(Protocolo.RESERVA_CANCELAR, id, funcionarioId);
    }

    @SuppressWarnings("unchecked")
    @Override public List<RecursoInfo> calendarizacionRecursos(String idCategoria) throws Exception {
        return (List<RecursoInfo>) enviar(Protocolo.CALENDARIZACION_RECURSOS, idCategoria);
    }
    @SuppressWarnings("unchecked")
    @Override public Map<String, Map<Integer, Celda>> calendarizacionMatriz(String fecha, List<RecursoInfo> recursos) throws Exception {
        return (Map<String, Map<Integer, Celda>>) enviar(Protocolo.CALENDARIZACION_MATRIZ, fecha, new ArrayList<>(recursos));
    }
    @SuppressWarnings("unchecked")
    @Override public Map<LocalDate, Map<Integer, Celda>> actividadMatrizSemana(String fecha) throws Exception {
        return (Map<LocalDate, Map<Integer, Celda>>) enviar(Protocolo.ACTIVIDAD_MATRIZ_SEMANA, fecha);
    }
    @SuppressWarnings("unchecked")
    @Override public Map<String, Integer> estadisticaRecursos(String desde, String hasta) throws Exception {
        return (Map<String, Integer>) enviar(Protocolo.ESTADISTICA_RECURSOS, desde, hasta);
    }
    @SuppressWarnings("unchecked")
    @Override public Map<LocalDate, Integer> estadisticaActividades(String desde, String hasta) throws Exception {
        return (Map<LocalDate, Integer>) enviar(Protocolo.ESTADISTICA_ACTIVIDADES, desde, hasta);
    }
}