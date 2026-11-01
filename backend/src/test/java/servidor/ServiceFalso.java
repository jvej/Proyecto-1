package servidor;

import modelo.*;
import protocolo.IService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

// Service "de mentira" para probar los sockets SIN base de datos.
class ServiceFalso implements IService {

    @Override public Usuario login(String id, String clave) throws Exception {
        if (!"1234".equals(clave)) throw new ValidacionException("ID o clave incorrectos.");
        return new Usuario(id, "", Rol.FUNCIONARIO);
    }
    @Override public void logout() { }
    @Override public void cambiarClave(String id, String a, String n, String c) { }
    @Override public List<Funcionario> funcionarioListar() { return new ArrayList<>(); }
    @Override public List<Funcionario> funcionarioBuscar(String t) { return new ArrayList<>(); }
    @Override public void funcionarioGuardar(String id, String n, String t, String c) { }
    @Override public void funcionarioEliminar(String id) { }
    @Override public List<Categoria> categoriaListar() {
        return new ArrayList<>(List.of(new Categoria("CAT-000001", "Sala para 10 personas")));
    }
    @Override public List<Categoria> categoriaBuscar(String t) { return categoriaListar(); }
    @Override public void categoriaGuardar(String id, String d) throws Exception {
        if (d == null || d.isBlank()) throw new ValidacionException("La descripción es obligatoria.");
    }
    @Override public void categoriaEliminar(String id) { }
    @Override public List<Recurso> recursoListar() { return new ArrayList<>(); }
    @Override public List<Recurso> recursoBuscar(String c, String d) { return new ArrayList<>(); }
    @Override public void recursoGuardar(String id, String c, String d) { }
    @Override public void recursoBorrar(String id) { }
    @Override public List<Reserva> misReservas(String f) { return new ArrayList<>(); }
    @Override public ResultadoReserva crearReserva(String funcionarioId, String actividad, String fecha, String hi,
                                                   String hf, List<String> categorias) {
        Reserva r = new Reserva("RES-000099", funcionarioId, actividad, LocalDate.parse(fecha),
                LocalTime.parse(hi), LocalTime.parse(hf), new ArrayList<>(categorias),
                new ArrayList<>(List.of("S1-001")), "ACTIVA");
        return ResultadoReserva.exito(r);
    }
    @Override public void cancelarReserva(String id, String f) { }
    @Override public List<RecursoInfo> calendarizacionRecursos(String c) { return new ArrayList<>(); }
    @Override public Map<String, Map<Integer, Celda>> calendarizacionMatriz(String f, List<RecursoInfo> r) { return new HashMap<>(); }
    @Override public Map<LocalDate, Map<Integer, Celda>> actividadMatrizSemana(String f) { return new HashMap<>(); }
    @Override public Map<String, Integer> estadisticaRecursos(String d, String h) { return new TreeMap<>(); }
    @Override public Map<LocalDate, Integer> estadisticaActividades(String d, String h) { return new TreeMap<>(); }
}