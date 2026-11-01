package logica;

import datos.FuncionarioDao;
import datos.RecursoDao;
import modelo.*;
import protocolo.IService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// El VERDADERO Service (vive solo en el Backend).
// Es una "fachada": un solo objeto que reune todo lo que el sistema sabe hacer
// y le delega el trabajo a cada service especifico.
// Los Worker (un hilo por cada Frontend conectado) llaman a este objeto.
public class Service implements IService {

    private static Service instancia;

    public static synchronized Service getInstancia() {
        if (instancia == null) instancia = new Service();
        return instancia;
    }

    private final UsuarioService usuarioService = new UsuarioService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final RecursoService recursoService = new RecursoService();
    private final ReservaService reservaService = new ReservaService();
    private final CalendarizacionService calendarizacionService = new CalendarizacionService();
    private final ActividadService actividadService = new ActividadService();
    private final EstadisticaService estadisticaService = new EstadisticaService();

    private final FuncionarioDao funcionarioDao = new FuncionarioDao();
    private final RecursoDao recursoDao = new RecursoDao();

    // ---------- Login ----------
    @Override public Usuario login(String id, String clave) throws Exception { return usuarioService.autenticar(id, clave); }
    @Override public void logout() { /* el Worker es quien olvida al usuario */ }
    @Override public void cambiarClave(String id, String actual, String nueva, String conf) throws Exception {
        usuarioService.cambiarClave(id, actual, nueva, conf);
    }

    // ---------- Funcionarios ----------
    @Override public List<Funcionario> funcionarioListar() throws Exception { return funcionarioService.listar(); }
    @Override public List<Funcionario> funcionarioBuscar(String texto) throws Exception { return funcionarioService.buscar(texto); }
    @Override public void funcionarioGuardar(String id, String nombre, String telefono, String clave) throws Exception {
        funcionarioService.guardar(id, nombre, telefono, clave);
    }
    @Override public void funcionarioEliminar(String id) throws Exception { funcionarioService.eliminar(id); }

    // ---------- Categorias ----------
    @Override public List<Categoria> categoriaListar() throws Exception { return categoriaService.listar(); }
    @Override public List<Categoria> categoriaBuscar(String texto) throws Exception { return categoriaService.buscar(texto); }
    @Override public void categoriaGuardar(String id, String descripcion) throws Exception { categoriaService.guardar(id, descripcion); }
    @Override public void categoriaEliminar(String id) throws Exception { categoriaService.eliminar(id); }

    // ---------- Recursos ----------
    @Override public List<Recurso> recursoListar() throws Exception { return recursoService.listar(); }
    @Override public List<Recurso> recursoBuscar(String categoriaId, String descripcion) throws Exception {
        return recursoService.buscar(categoriaId, descripcion);
    }
    @Override public void recursoGuardar(String id, String categoriaId, String descripcion) throws Exception {
        recursoService.guardar(id, categoriaId, descripcion);
    }
    @Override public void recursoBorrar(String id) throws Exception { recursoService.borrar(id); }

    // ---------- Reservas ----------
    @Override public List<Reserva> misReservas(String funcionarioId) throws Exception { return reservaService.misReservas(funcionarioId); }
    @Override public ResultadoReserva crearReserva(String funcionarioId, String actividad, String fecha, String horaInicio,
                                                   String horaFin, List<String> categoriasIds) throws Exception {
        return reservaService.crearReserva(funcionarioId, actividad, fecha, horaInicio, horaFin, categoriasIds);
    }
    @Override public void cancelarReserva(String id, String funcionarioId) throws Exception {
        reservaService.cancelarReserva(id, funcionarioId);
    }

    // ---------- Calendarizacion / Actividades / Estadisticas ----------
    @Override public List<RecursoInfo> calendarizacionRecursos(String idCategoria) throws Exception {
        return calendarizacionService.obtenerRecursos(idCategoria);
    }
    @Override public Map<String, Map<Integer, Celda>> calendarizacionMatriz(String fecha, List<RecursoInfo> recursos) throws Exception {
        return calendarizacionService.obtenerMatriz(fecha, recursos);
    }
    @Override public Map<LocalDate, Map<Integer, Celda>> actividadMatrizSemana(String fecha) throws Exception {
        return actividadService.obtenerMatrizSemana(fecha);
    }
    @Override public Map<String, Integer> estadisticaRecursos(String desde, String hasta) throws Exception {
        return estadisticaService.estadisticasRecursosPorCategoria(desde, hasta);
    }
    @Override public Map<LocalDate, Integer> estadisticaActividades(String desde, String hasta) throws Exception {
        return estadisticaService.estadisticasActividadesPorSemana(desde, hasta);
    }

    // ---------- Alertas ----------
    // Arma la alerta "lista para mostrar" que se envia a los demas Frontend.
    public Alerta crearAlerta(Reserva r) throws SQLException {
        Funcionario f = funcionarioDao.buscarPorId(r.getFuncionarioId());
        String encargado = (f != null) ? f.getNombre() : r.getFuncionarioId();

        List<String> recursos = new ArrayList<>();
        for (String idRecurso : r.getRecursosIds()) {
            Recurso rec = recursoDao.buscarPorId(idRecurso);
            recursos.add(rec != null ? rec.getDescripcion() + " (" + idRecurso + ")" : idRecurso);
        }
        return new Alerta(r.getId(), encargado, r.getActividad(), r.getFecha(), r.getHoraInicio(), r.getHoraFin(), recursos);
    }
}