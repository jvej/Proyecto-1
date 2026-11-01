package logica;

import datos.ReservaDao;
import modelo.Celda;
import modelo.Reserva;
import modelo.ValidacionException;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ActividadService {

    private final ReservaDao reservaDao = new ReservaDao();

    public static LocalDate calcularLunesDeSemana(LocalDate referencia) {
        return referencia.minusDays(referencia.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

    // Para cada dia de la semana: hora -> actividad.
    public Map<LocalDate, Map<Integer, Celda>> obtenerMatrizSemana(String textoFecha) throws ValidacionException, SQLException {
        LocalDate referencia = Validaciones.fecha(textoFecha, "Fecha inválida. Use el formato yyyy-MM-dd (ej: 2026-08-14).");
        LocalDate lunes = calcularLunesDeSemana(referencia);
        LocalDate domingo = lunes.plusDays(6);

        Map<LocalDate, Map<Integer, Celda>> matriz = new HashMap<>();
        for (int i = 0; i < 7; i++) matriz.put(lunes.plusDays(i), new HashMap<>());

        for (Reserva r : reservaDao.listarActivasEntre(lunes, domingo)) {
            Map<Integer, Celda> columnaDia = matriz.get(r.getFecha());
            for (int h = r.getHoraInicio().getHour(); h < r.getHoraFin().getHour(); h++) {
                columnaDia.put(h, new Celda(r.getActividad(), r.getFuncionarioId()));
            }
        }
        return matriz;
    }
}