package util;

import modelo.Reserva;
import modelo.ValidacionException;
import persistence.ReservaXMLPersistence;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class ActividadService {

    public static final int HORA_DESDE = 6;
    public static final int HORA_HASTA = 21;

    private final ReservaXMLPersistence persistence = new ReservaXMLPersistence();

    public static class Celda {
        public final String actividad, idFuncionario;
        public Celda(String actividad, String idFuncionario) {
            this.actividad = actividad; this.idFuncionario = idFuncionario;
        }
    }

    public LocalDate validarFecha(String textoFecha) throws ValidacionException {
        try {
            return LocalDate.parse(textoFecha.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new ValidacionException("Fecha inválida. Use el formato yyyy-MM-dd (ej: 2026-08-14).");
        }
    }

    public LocalDate calcularLunesDeSemana(LocalDate referencia) {
        int diasDesdeElLunes = referencia.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        return referencia.minusDays(diasDesdeElLunes);
    }

    public Map<LocalDate, Map<Integer, Celda>> obtenerMatrizSemana(String textoFecha) throws ValidacionException {
        LocalDate lunes = calcularLunesDeSemana(validarFecha(textoFecha));
        LocalDate domingo = lunes.plusDays(6);

        Map<LocalDate, Map<Integer, Celda>> matriz = new HashMap<>();
        for (int i = 0; i < 7; i++) matriz.put(lunes.plusDays(i), new HashMap<>());

        for (Reserva r : persistence.readAll()) {
            if (!"ACTIVA".equals(r.getEstado())) continue;
            LocalDate fecha = r.getFecha();
            if (fecha.isBefore(lunes) || fecha.isAfter(domingo)) continue;

            int horaDesde = r.getHoraInicio().getHour();
            int horaHasta = r.getHoraFin().getHour();
            Map<Integer, Celda> columnaDia = matriz.get(fecha);
            for (int h = horaDesde; h < horaHasta; h++) {
                columnaDia.put(h, new Celda(r.getActividad(), r.getFuncionarioId()));
            }
        }
        return matriz;
    }
}