package modelo;

import java.io.Serializable;

// Datos minimos de un recurso para la pantalla de Calendarizacion.
public class RecursoInfo implements Serializable {
    public final String id;
    public final String idCategoria;
    public final String descripcion;

    public RecursoInfo(String id, String idCategoria, String descripcion) {
        this.id = id;
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
    }
}