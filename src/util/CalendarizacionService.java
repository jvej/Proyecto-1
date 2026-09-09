package util;

import modelo.Categoria;
import modelo.Reserva;
import modelo.ValidacionException;
import persistence.CategoriaXMLPersistence;
import persistence.RecursoXMLPersistence;
import persistence.ReservaXMLPersistence;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarizacionService {

    private final CategoriaXMLPersistence categoriaPersistence = new CategoriaXMLPersistence();
    private final RecursoXMLPersistence recursoPersistence = new RecursoXMLPersistence();
    private final ReservaXMLPersistence reservaPersistence = new ReservaXMLPersistence();

    public static class RecursoInfo {
        public final String id, idCategoria, descripcion;
        public RecursoInfo(String id, String idCategoria, String descripcion) {
            this.id = id; this.idCategoria = idCategoria; this.descripcion = descripcion;
        }
    }

    public static class Celda {
        public final String actividad, idFuncionario;
        public Celda(String actividad, String idFuncionario) {
            this.actividad = actividad; this.idFuncionario = idFuncionario;
        }
    }

    public List<Categoria> listarCategorias() { return categoriaPersistence.readAll(); }

    public List<RecursoInfo> obtenerRecursos(String idCategoria) throws ValidacionException {
        if (idCategoria == null || idCategoria.trim().isEmpty()) throw new ValidacionException("Debe seleccionar una categoría.");
        return recursoPersistence.readAll().stream()
                .filter(r -> r.getCategoriaId().equalsIgnoreCase(idCategoria.trim()))
                .map(r -> new RecursoInfo(r.getId(), r.getCategoriaId(), r.getDescripcion()))
                .collect(Collectors.toList());
    }

    public LocalDate validarFecha(String textoFecha) throws ValidacionException {
        try {
            return LocalDate.parse(textoFecha.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new ValidacionException("Fecha inválida. Use el formato yyyy-MM-dd (ej: 2026-08-14).");
        }
    }

    public Map<String, Map<Integer, Celda>> obtenerMatriz(String textoFecha, List<RecursoInfo> recursos) throws ValidacionException {
        LocalDate fecha = validarFecha(textoFecha);
        List<String> idsRecurso = recursos.stream().map(r -> r.id).collect(Collectors.toList());

        Map<String, Map<Integer, Celda>> matriz = new HashMap<>();
        for (RecursoInfo r : recursos) matriz.put(r.id, new HashMap<>());

        for (Reserva r : reservaPersistence.readAll()) {
            if (!"ACTIVA".equals(r.getEstado()) || !r.getFecha().equals(fecha)) continue;
            for (String idRecurso : r.getRecursosIds()) {
                if (!idsRecurso.contains(idRecurso)) continue;
                int horaDesde = r.getHoraInicio().getHour();
                int horaHasta = r.getHoraFin().getHour();
                for (int h = horaDesde; h < horaHasta; h++) {
                    matriz.get(idRecurso).put(h, new Celda(r.getActividad(), r.getFuncionarioId()));
                }
            }
        }
        return matriz;
    }
}