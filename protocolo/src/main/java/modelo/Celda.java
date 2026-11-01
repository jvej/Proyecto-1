package modelo;

import java.io.Serializable;

// Una "celda" de las matrices de Calendarizacion y Actividades:
// que actividad ocupa esa hora y quien la reservo.
public class Celda implements Serializable {
    public final String actividad;
    public final String idFuncionario;

    public Celda(String actividad, String idFuncionario) {
        this.actividad = actividad;
        this.idFuncionario = idFuncionario;
    }
}