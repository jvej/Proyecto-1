package logica;

import datos.RecursoDao;
import datos.ReservaDao;
import modelo.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarizacionService {

    private final RecursoDao recursoDao = new RecursoDao();
    private final ReservaDao reservaDao = new ReservaDao();

    public List<RecursoInfo> obtenerRecursos(String idCategoria) throws ValidacionException, SQLException {
        if (Validaciones.vacio(idCategoria)) throw new ValidacionException("Debe seleccionar una categoría.");
        List<RecursoInfo> recursos = recursoDao.listarPorCategoria(idCategoria.trim()).stream()
                .map(r -> new RecursoInfo(r.getId(), r.getCategoriaId(), r.getDescripcion()))
                .collect(Collectors.toList());
        if (recursos.isEmpty()) throw new ValidacionException("Esa categoría no tiene recursos registrados todavía.");
        return recursos;
    }

    // Para cada recurso: hora -> quien lo tiene ocupado.
    public Map<String, Map<Integer, Celda>> obtenerMatriz(String textoFecha, List<RecursoInfo> recursos)
            throws ValidacionException, SQLException {
        LocalDate fecha = Validaciones.fecha(textoFecha, "Fecha inválida. Use el formato yyyy-MM-dd (ej: 2026-08-14).");
        if (recursos == null || recursos.isEmpty()) throw new ValidacionException("No hay recursos que mostrar.");

        Map<String, Map<Integer, Celda>> matriz = new HashMap<>();
        for (RecursoInfo r : recursos) matriz.put(r.id, new HashMap<>());

        for (Reserva r : reservaDao.listarActivasEntre(fecha, fecha)) {
            for (String idRecurso : r.getRecursosIds()) {
                Map<Integer, Celda> fila = matriz.get(idRecurso);
                if (fila == null) continue; // recurso de otra categoria
                for (int h = r.getHoraInicio().getHour(); h < r.getHoraFin().getHour(); h++) {
                    fila.put(h, new Celda(r.getActividad(), r.getFuncionarioId()));
                }
            }
        }
        return matriz;
    }
}