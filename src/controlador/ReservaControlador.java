package controlador;

import modelo.Reserva;
import modelo.ValidacionException;
import util.ReservaService;
import util.ReservaService.ResultadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaControlador {
    private final ReservaService reservaService = new ReservaService();

    public List<Reserva> misReservas(String funcionarioId) { return reservaService.misReservas(funcionarioId); }

    public ResultadoReserva crearReserva(String funcionarioId, String actividad, LocalDate fecha,
                                         LocalTime horaInicio, LocalTime horaFin,
                                         List<String> categoriasIds) throws ValidacionException {
        return reservaService.crearReserva(funcionarioId, actividad, fecha, horaInicio, horaFin, categoriasIds);
    }

    public void cancelarReserva(String id, String funcionarioId) throws ValidacionException {
        reservaService.cancelarReserva(id, funcionarioId);
    }
}