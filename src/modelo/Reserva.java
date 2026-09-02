package modelo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    private String id;
    private String funcionarioId;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> categoriasIds;
    private List<String> recursosIds;
    private String estado;

    public Reserva() {
        this.categoriasIds = new ArrayList<>();
        this.recursosIds = new ArrayList<>();
    }

    public Reserva(String id, String funcionarioId, String actividad,
                   LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                   List<String> categoriasIds, List<String> recursosIds, String estado) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.categoriasIds = categoriasIds;
        this.recursosIds = recursosIds;
        this.estado = estado;
    }

    public String getId() { return id; }
    public String getFuncionarioId() { return funcionarioId; }
    public String getActividad() { return actividad; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public List<String> getCategoriasIds() { return categoriasIds; }
    public List<String> getRecursosIds() { return recursosIds; }
    public String getEstado() { return estado; }

    public void setId(String id) { this.id = id; }
    public void setFuncionarioId(String funcionarioId) { this.funcionarioId = funcionarioId; }
    public void setActividad(String actividad) { this.actividad = actividad; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public void setCategoriasIds(List<String> categoriasIds) { this.categoriasIds = categoriasIds; }
    public void setRecursosIds(List<String> recursosIds) { this.recursosIds = recursosIds; }
    public void setEstado(String estado) { this.estado = estado; }
}
