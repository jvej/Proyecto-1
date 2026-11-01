package controlador;

import modelo.ValidacionException;
import util.EstadisticaService;

import java.time.LocalDate;
import java.util.Map;

public class EstadisticaControlador {
    private final EstadisticaService estadisticaService = new EstadisticaService();

    public Map<String, Integer> estadisticasRecursosPorCategoria(String textoDesde, String textoHasta) throws ValidacionException {
        return estadisticaService.estadisticasRecursosPorCategoria(textoDesde, textoHasta);
    }

    public Map<LocalDate, Integer> estadisticasActividadesPorSemana(String textoDesde, String textoHasta) throws ValidacionException {
        return estadisticaService.estadisticasActividadesPorSemana(textoDesde, textoHasta);
    }
}