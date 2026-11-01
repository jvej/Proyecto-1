package controlador;

import modelo.Reserva;
import modelo.ValidacionException;
import util.ReservaService;
import util.ReservaService.ResultadoReserva;

import java.util.List;

public class ReservaControlador {
    private final ReservaService reservaService = new ReservaService();

    public List<Reserva> misReservas(String funcionarioId) { return reservaService.misReservas(funcionarioId); }

    public ResultadoReserva crearReserva(String funcionarioId, String actividad, String textoFecha,
                                         String textoHoraInicio, String textoHoraFin,
                                         List<String> categoriasIds) throws ValidacionException {
        return reservaService.crearReserva(funcionarioId, actividad, textoFecha, textoHoraInicio, textoHoraFin, categoriasIds);
    }

    public void cancelarReserva(String id, String funcionarioId) throws ValidacionException {
        reservaService.cancelarReserva(id, funcionarioId);
    }
}