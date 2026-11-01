package controlador;

import java.time.LocalDate;
import java.util.Map;

public class EstadisticaControlador {

    public Map<String, Integer> estadisticasRecursosPorCategoria(String desde, String hasta) throws Exception {
        return Remoto.service().estadisticaRecursos(desde, hasta);
    }

    public Map<LocalDate, Integer> estadisticasActividadesPorSemana(String desde, String hasta) throws Exception {
        return Remoto.service().estadisticaActividades(desde, hasta);
    }
}