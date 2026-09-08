package controlador;

import dao.ReservaDAO;
import modelo.Reserva;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class ActividadControlador {

    // Mismo rango de horas que usa Calendarizacion, para que las matrices se vean consistentes.
    public static final int HORA_DESDE = 6;
    public static final int HORA_HASTA = 21;

    private final ReservaDAO reservaDAO = new ReservaDAO();

    // Una celda de la matriz: qué actividad/funcionario está agendado ese día y hora (o null si está libre).
    public static class Celda {
        public final String actividad;
        public final String idFuncionario;

        public Celda(String actividad, String idFuncionario) {
            this.actividad = actividad;
            this.idFuncionario = idFuncionario;
        }
    }

    // Valida el formato de fecha antes de usarlo (se espera yyyy-MM-dd), igual que Calendarizacion.
    public LocalDate validarFecha(String textoFecha) throws Exception {
        try {
            return LocalDate.parse(textoFecha.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new Exception("Fecha inválida. Use el formato yyyy-MM-dd (ej: 2026-08-14).");
        }
    }

    // Dada cualquier fecha de referencia, devuelve el lunes de esa semana.
    public LocalDate calcularLunesDeSemana(LocalDate referencia) {
        int diasDesdeElLunes = referencia.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        return referencia.minusDays(diasDesdeElLunes);
    }

    /**
     * Arma la matriz semanal: para cada día (lunes a domingo, a partir de textoFecha)
     * y cada hora del rango, la celda ocupada (o null si está libre).
     * Solo lee reservas ACTIVAS; una reserva CANCELADA no cuenta como "programada".
     */
    public Map<LocalDate, Map<Integer, Celda>> obtenerMatrizSemana(String textoFecha) throws Exception {
        LocalDate lunes = calcularLunesDeSemana(validarFecha(textoFecha));
        LocalDate domingo = lunes.plusDays(6);

        Map<LocalDate, Map<Integer, Celda>> matriz = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            matriz.put(lunes.plusDays(i), new HashMap<>());
        }

        for (Reserva r : reservaDAO.listar()) {
            if (!"ACTIVA".equals(r.getEstado())) continue;
            LocalDate fecha = r.getFecha();
            if (fecha.isBefore(lunes) || fecha.isAfter(domingo)) continue;

            try {
                int horaDesde = r.getHoraInicio().getHour();
                int horaHasta = r.getHoraFin().getHour();
                Map<Integer, Celda> columnaDia = matriz.get(fecha);
                for (int h = horaDesde; h < horaHasta; h++) {
                    columnaDia.put(h, new Celda(r.getActividad(), r.getFuncionarioId()));
                }
            } catch (Exception ignorado) {
                // Reserva con hora en formato inesperado; se ignora esa reserva puntual.
            }
        }
        return matriz;
    }
}
