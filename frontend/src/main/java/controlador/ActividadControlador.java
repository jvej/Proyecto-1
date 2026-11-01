package controlador;

import modelo.ValidacionException;
import util.ActividadService;
import util.ActividadService.Celda;

import java.time.LocalDate;
import java.util.Map;

public class ActividadControlador {
    private final ActividadService actividadService = new ActividadService();

    public Map<LocalDate, Map<Integer, Celda>> obtenerMatrizSemana(String textoFecha) throws ValidacionException {
        return actividadService.obtenerMatrizSemana(textoFecha);
    }
}