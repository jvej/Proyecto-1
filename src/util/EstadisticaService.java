package util;

import modelo.Categoria;
import modelo.Reserva;
import modelo.ValidacionException;
import persistence.CategoriaXMLPersistence;
import persistence.ReservaXMLPersistence;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EstadisticaService {

    private final ReservaXMLPersistence reservaPersistence = new ReservaXMLPersistence();
    private final CategoriaXMLPersistence categoriaPersistence = new CategoriaXMLPersistence();
    private final ActividadService actividadService = new ActividadService();

    public LocalDate[] validarRango(String textoDesde, String textoHasta) throws ValidacionException {
        LocalDate desde, hasta;
        try { desde = LocalDate.parse(textoDesde.trim()); }
        catch (DateTimeParseException | NullPointerException e) { throw new ValidacionException("Fecha 'desde' inválida. Use el formato yyyy-MM-dd."); }
        try { hasta = LocalDate.parse(textoHasta.trim()); }
        catch (DateTimeParseException | NullPointerException e) { throw new ValidacionException("Fecha 'hasta' inválida. Use el formato yyyy-MM-dd."); }
        if (desde.isAfter(hasta)) throw new ValidacionException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        return new LocalDate[]{desde, hasta};
    }

    public Map<String, Integer> estadisticasRecursosPorCategoria(String textoDesde, String textoHasta) throws ValidacionException {
        LocalDate[] rango = validarRango(textoDesde, textoHasta);
        Map<String, Integer> conteo = new TreeMap<>();

        for (Reserva r : reservaPersistence.readAll()) {
            if (!"ACTIVA".equals(r.getEstado())) continue;
            LocalDate fecha = r.getFecha();
            if (fecha.isBefore(rango[0]) || fecha.isAfter(rango[1])) continue;

            List<String> categoriasIds = r.getCategoriasIds();
            if (categoriasIds == null) continue;
            for (String idCategoria : categoriasIds) {
                Categoria categoria = buscarCategoria(idCategoria);
                String descripcion = (categoria != null) ? categoria.getDescripcion() : idCategoria;
                conteo.merge(descripcion, 1, Integer::sum);
            }
        }
        return conteo;
    }

    public Map<LocalDate, Integer> estadisticasActividadesPorSemana(String textoDesde, String textoHasta) throws ValidacionException {
        LocalDate[] rango = validarRango(textoDesde, textoHasta);
        Map<LocalDate, Integer> conteo = new TreeMap<>();

        for (Reserva r : reservaPersistence.readAll()) {
            if (!"ACTIVA".equals(r.getEstado())) continue;
            LocalDate fecha = r.getFecha();
            if (fecha.isBefore(rango[0]) || fecha.isAfter(rango[1])) continue;
            conteo.merge(actividadService.calcularLunesDeSemana(fecha), 1, Integer::sum);
        }
        return conteo;
    }

    private Categoria buscarCategoria(String id) {
        for (Categoria c : categoriaPersistence.readAll()) if (c.getId().equalsIgnoreCase(id)) return c;
        return null;
    }
}