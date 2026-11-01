package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Lo que el Backend envia (asincronicamente) a los demas Frontend cada vez
// que alguien crea una reserva. Trae los datos "listos para mostrar":
// el nombre del encargado y la descripcion de los recursos asignados.
public class Alerta implements Serializable {
    private String reservaId;
    private String encargado;      // nombre del funcionario que reservo
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> recursos = new ArrayList<>(); // "Sala 1, primer piso (S1-001)"

    public Alerta() { }

    public Alerta(String reservaId, String encargado, String actividad, LocalDate fecha,
                  LocalTime horaInicio, LocalTime horaFin, List<String> recursos) {
        this.reservaId = reservaId;
        this.encargado = encargado;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursos = new ArrayList<>(recursos);
    }

    public String getReservaId() { return reservaId; }
    public String getEncargado() { return encargado; }
    public String getActividad() { return actividad; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public List<String> getRecursos() { return recursos; }

    // Texto que se ve en la lista de alertas: "Juan Mora - Reunion clientes"
    public String getTitulo() { return encargado + " - " + actividad; }

    @Override
    public String toString() { return getTitulo(); }
}