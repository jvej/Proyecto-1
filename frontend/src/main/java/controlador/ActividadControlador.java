package controlador;

import modelo.Celda;

import java.time.LocalDate;
import java.util.Map;

public class ActividadControlador {
    // Horas que se muestran en la tabla de actividades (antes estaban en ActividadService).
    public static final int HORA_DESDE = 6;
    public static final int HORA_HASTA = 21;

    public Map<LocalDate, Map<Integer, Celda>> obtenerMatrizSemana(String textoFecha) throws Exception {
        return Remoto.service().actividadMatrizSemana(textoFecha);
    }
}