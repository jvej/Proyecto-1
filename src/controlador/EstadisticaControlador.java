package controlador;

import dao.CategoriaDAO;
import dao.CategoriaDAOImpl;
import dao.ReservaDAO;
import modelo.Categoria;
import modelo.Reserva;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EstadisticaControlador {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();

    // Valida que "desde" y "hasta" sean fechas correctas y que desde <= hasta.
    public LocalDate[] validarRango(String textoDesde, String textoHasta) throws Exception {
        LocalDate desde;
        LocalDate hasta;
        try {
            desde = LocalDate.parse(textoDesde.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new Exception("Fecha 'desde' inválida. Use el formato yyyy-MM-dd.");
        }
        try {
            hasta = LocalDate.parse(textoHasta.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new Exception("Fecha 'hasta' inválida. Use el formato yyyy-MM-dd.");
        }
        if (desde.isAfter(hasta)) {
            throw new Exception("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return new LocalDate[]{desde, hasta};
    }

    // Cuenta cuántas veces se usó cada categoría en el rango dado. Si una reserva tiene varias, cada una suma por separado.
    public Map<String, Integer> estadisticasRecursosPorCategoria(String textoDesde, String textoHasta) throws Exception {
        LocalDate[] rango = validarRango(textoDesde, textoHasta);
        LocalDate desde = rango[0];
        LocalDate hasta = rango[1];

        // TreeMap para que el resultado salga ordenado alfabeticamente por descripcion (mas facil de leer).
        Map<String, Integer> conteo = new TreeMap<>();

        for (Reserva r : reservaDAO.listar()) {
            if (!"ACTIVA".equals(r.getEstado())) continue;
            LocalDate fecha = r.getFecha();
            if (fecha.isBefore(desde) || fecha.isAfter(hasta)) continue;

            List<String> categoriasIds = r.getCategoriasIds();
            if (categoriasIds == null) continue;

            for (String idCategoria : categoriasIds) {
                Categoria categoria = categoriaDAO.buscarPorId(idCategoria);
                String descripcion = (categoria != null) ? categoria.getDescripcion() : idCategoria;
                conteo.merge(descripcion, 1, Integer::sum);
            }
        }
        return conteo;
    }

    // Cuenta cuántas veces se usó cada categoría en el rango dado. Si una reserva tiene varias, cada una suma por separado.
    public Map<LocalDate, Integer> estadisticasActividadesPorSemana(String textoDesde, String textoHasta) throws Exception {
        LocalDate[] rango = validarRango(textoDesde, textoHasta);
        LocalDate desde = rango[0];
        LocalDate hasta = rango[1];

        Map<LocalDate, Integer> conteo = new TreeMap<>();

        ActividadControlador auxiliar = new ActividadControlador();
        for (Reserva r : reservaDAO.listar()) {
            if (!"ACTIVA".equals(r.getEstado())) continue;
            LocalDate fecha = r.getFecha();
            if (fecha.isBefore(desde) || fecha.isAfter(hasta)) continue;

            LocalDate lunesDeEsaSemana = auxiliar.calcularLunesDeSemana(fecha);
            conteo.merge(lunesDeEsaSemana, 1, Integer::sum);
        }
        return conteo;
    }
}
