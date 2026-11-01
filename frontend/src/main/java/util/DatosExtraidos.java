package util;
import java.util.ArrayList;
import java.util.List;

public class DatosExtraidos {
    private String actividad = "";
    private String fecha = "";
    private String horaInicio = "";
    private String horaFin = "";
    private List<String> categoriasDescripciones = new ArrayList<>();

    public String getActividad() { return actividad; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public List<String> getCategoriasDescripciones() { return categoriasDescripciones; }

    public void setActividad(String actividad) { this.actividad = actividad; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
    public void setCategoriasDescripciones(List<String> categoriasDescripciones) { this.categoriasDescripciones = categoriasDescripciones; }
}
