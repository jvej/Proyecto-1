package protocolo;

import modelo.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// "Contrato" de todo lo que el Backend sabe hacer.
//  - En el Backend lo implementa logica.Service (el verdadero, usa la BD).
//  - En el Frontend lo implementa proxy.ServiceProxy (el representante local,
//    que en vez de hacer el trabajo, lo manda por el socket).  <- Patron Proxy
public interface IService {

    // Login / clave
    Usuario login(String id, String clave) throws Exception;
    void logout() throws Exception;
    void cambiarClave(String id, String claveActual, String claveNueva, String confirmacion) throws Exception;

    // Funcionarios
    List<Funcionario> funcionarioListar() throws Exception;
    List<Funcionario> funcionarioBuscar(String texto) throws Exception;
    void funcionarioGuardar(String id, String nombre, String telefono, String clave) throws Exception;
    void funcionarioEliminar(String id) throws Exception;

    // Categorias
    List<Categoria> categoriaListar() throws Exception;
    List<Categoria> categoriaBuscar(String texto) throws Exception;
    void categoriaGuardar(String id, String descripcion) throws Exception;
    void categoriaEliminar(String id) throws Exception;

    // Recursos
    List<Recurso> recursoListar() throws Exception;
    List<Recurso> recursoBuscar(String categoriaId, String descripcion) throws Exception;
    void recursoGuardar(String id, String categoriaId, String descripcion) throws Exception;
    void recursoBorrar(String id) throws Exception;

    // Reservas
    List<Reserva> misReservas(String funcionarioId) throws Exception;
    ResultadoReserva crearReserva(String funcionarioId, String actividad, String fecha,
                                  String horaInicio, String horaFin, List<String> categoriasIds) throws Exception;
    void cancelarReserva(String id, String funcionarioId) throws Exception;

    // Calendarizacion
    List<RecursoInfo> calendarizacionRecursos(String idCategoria) throws Exception;
    Map<String, Map<Integer, Celda>> calendarizacionMatriz(String fecha, List<RecursoInfo> recursos) throws Exception;

    // Actividades
    Map<LocalDate, Map<Integer, Celda>> actividadMatrizSemana(String fecha) throws Exception;

    // Estadisticas
    Map<String, Integer> estadisticaRecursos(String desde, String hasta) throws Exception;
    Map<LocalDate, Integer> estadisticaActividades(String desde, String hasta) throws Exception;
}