package controlador;

import modelo.Categoria;
import modelo.Celda;
import modelo.RecursoInfo;

import java.util.List;
import java.util.Map;

public class CalendarizacionControlador {

    public List<Categoria> listarCategorias() {
        try { return Remoto.service().categoriaListar(); } catch (Exception e) { throw Remoto.error(e); }
    }
    public List<RecursoInfo> obtenerRecursos(String idCategoria) throws Exception {
        return Remoto.service().calendarizacionRecursos(idCategoria);
    }
    public Map<String, Map<Integer, Celda>> obtenerMatriz(String textoFecha, List<RecursoInfo> recursos) throws Exception {
        return Remoto.service().calendarizacionMatriz(textoFecha, recursos);
    }
}