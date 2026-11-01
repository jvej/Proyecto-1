package logica;

import datos.ReservaDao;
import modelo.ValidacionException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class EstadisticaService {

    private final ReservaDao reservaDao = new ReservaDao();

    public LocalDate[] validarRango(String textoDesde, String textoHasta) throws ValidacionException {
        LocalDate desde = Validaciones.fecha(textoDesde, "Fecha 'desde' inválida. Use el formato yyyy-MM-dd.");
        LocalDate hasta = Validaciones.fecha(textoHasta, "Fecha 'hasta' inválida. Use el formato yyyy-MM-dd.");
        if (desde.isAfter(hasta)) throw new ValidacionException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        return new LocalDate[]{desde, hasta};
    }

    public Map<String, Integer> estadisticasRecursosPorCategoria(String desde, String hasta) throws ValidacionException, SQLException {
        LocalDate[] rango = validarRango(desde, hasta);
        return reservaDao.contarPorCategoria(rango[0], rango[1]);
    }

    public Map<LocalDate, Integer> estadisticasActividadesPorSemana(String desde, String hasta) throws ValidacionException, SQLException {
        LocalDate[] rango = validarRango(desde, hasta);
        return reservaDao.contarPorSemana(rango[0], rango[1]);
    }
}