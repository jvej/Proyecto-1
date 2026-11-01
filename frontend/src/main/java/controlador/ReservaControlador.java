package controlador;

import modelo.Reserva;
import modelo.ResultadoReserva;

import java.util.List;

public class ReservaControlador {

    public List<Reserva> misReservas(String funcionarioId) {
        try { return Remoto.service().misReservas(funcionarioId); } catch (Exception e) { throw Remoto.error(e); }
    }

    public ResultadoReserva crearReserva(String funcionarioId, String actividad, String textoFecha,
                                         String textoHoraInicio, String textoHoraFin,
                                         List<String> categoriasIds) throws Exception {
        return Remoto.service().crearReserva(funcionarioId, actividad, textoFecha, textoHoraInicio, textoHoraFin, categoriasIds);
    }

    public void cancelarReserva(String id, String funcionarioId) throws Exception {
        Remoto.service().cancelarReserva(id, funcionarioId);
    }
}