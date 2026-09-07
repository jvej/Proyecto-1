package controlador;
import modelo.Reserva;
import java.util.List;


public class ResultadoReserva {
    private boolean exito;
    private Reserva reserva;
    private List<String> categoriasNoDisponibles;

    public static ResultadoReserva exito(Reserva reserva){
        ResultadoReserva r = new ResultadoReserva();
        r.exito =true;
        r.reserva = reserva;
        return r;
    }

    public static ResultadoReserva fallo(List<String> categoriasNoDisponibles) {
        ResultadoReserva r =new ResultadoReserva();
        r.exito =false;
        r.categoriasNoDisponibles =categoriasNoDisponibles;
        return r;
    }

    public boolean isExito() { return exito; }
    public Reserva getReserva() { return reserva; }
    public List<String> getCategoriasNoDisponibles() { return categoriasNoDisponibles; }
}
