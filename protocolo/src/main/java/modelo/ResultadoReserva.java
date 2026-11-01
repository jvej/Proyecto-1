package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Resultado de intentar crear una reserva: o tuvo exito (trae la reserva),
// o fallo porque algunas categorias no tenian recursos libres.
public class ResultadoReserva implements Serializable {
    private boolean exito;
    private Reserva reserva;
    private List<String> categoriasNoDisponibles = new ArrayList<>();

    public static ResultadoReserva exito(Reserva reserva) {
        ResultadoReserva r = new ResultadoReserva();
        r.exito = true;
        r.reserva = reserva;
        return r;
    }

    public static ResultadoReserva fallo(List<String> categoriasNoDisponibles) {
        ResultadoReserva r = new ResultadoReserva();
        r.exito = false;
        r.categoriasNoDisponibles = new ArrayList<>(categoriasNoDisponibles);
        return r;
    }

    public boolean isExito() { return exito; }
    public Reserva getReserva() { return reserva; }
    public List<String> getCategoriasNoDisponibles() { return categoriasNoDisponibles; }
}