package controlador;

import modelo.Categoria;
import modelo.ValidacionException;
import util.CalendarizacionService;
import util.CalendarizacionService.Celda;
import util.CalendarizacionService.RecursoInfo;

import java.util.List;
import java.util.Map;

public class CalendarizacionControlador {
    private final CalendarizacionService calendarizacionService = new CalendarizacionService();

    public List<Categoria> listarCategorias() { return calendarizacionService.listarCategorias(); }
    public List<RecursoInfo> obtenerRecursos(String idCategoria) throws ValidacionException { return calendarizacionService.obtenerRecursos(idCategoria); }
    public Map<String, Map<Integer, Celda>> obtenerMatriz(String textoFecha, List<RecursoInfo> recursos) throws ValidacionException {
        return calendarizacionService.obtenerMatriz(textoFecha, recursos);
    }
}