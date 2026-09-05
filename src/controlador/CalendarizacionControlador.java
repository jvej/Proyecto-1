package controlador;

import dao.CategoriaDAO;
import dao.CategoriaDAOImpl;
import dao.RecursoLecturaDAO;
import dao.RecursoLecturaDAO.RecursoInfo;
import dao.RecursoLecturaDAOImpl;
import dao.ReservaLecturaDAO;
import dao.ReservaLecturaDAO.ReservaInfo;
import dao.ReservaLecturaDAOImpl;
import modelo.Categoria;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarizacionControlador {

    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    private final RecursoLecturaDAO recursoDAO = new RecursoLecturaDAOImpl();
    private final ReservaLecturaDAO reservaDAO = new ReservaLecturaDAOImpl();

    // Una celda de la matriz: qué actividad/funcionario ocupa un recurso a cierta hora (o null si está libre).
    public static class Celda {
        public final String actividad;
        public final String idFuncionario;
        public Celda(String actividad, String idFuncionario) {
            this.actividad = actividad;
            this.idFuncionario = idFuncionario;
        }
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    public List<RecursoInfo> obtenerRecursos(String idCategoria) throws Exception {
        if (idCategoria == null || idCategoria.trim().isEmpty()) {
            throw new Exception("Debe seleccionar una categoría.");
        }
        return recursoDAO.listarPorCategoria(idCategoria.trim());
    }

    // Valida el formato de fecha antes de usarlo (se espera yyyy-MM-dd).
    public LocalDate validarFecha(String textoFecha) throws Exception {
        try {
            return LocalDate.parse(textoFecha.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new Exception("Fecha inválida. Use el formato yyyy-MM-dd (ej: 2026-08-14).");
        }
    }

    /** Devuelve, para cada recurso y cada hora (6 a 21), la celda ocupada (o null si está libre). */
    public Map<String, Map<Integer, Celda>> obtenerMatriz(String textoFecha, List<RecursoInfo> recursos) throws Exception {
        LocalDate fecha = validarFecha(textoFecha);

        List<String> idsRecurso = recursos.stream().map(r -> r.id).collect(Collectors.toList());
        List<ReservaInfo> reservas = reservaDAO.listarPorFecha(fecha.toString(), idsRecurso);

        Map<String, Map<Integer, Celda>> matriz = new HashMap<>();
        for (RecursoInfo r : recursos) {
            matriz.put(r.id, new HashMap<>());
        }

        for (ReservaInfo res : reservas) {
            try {
                int horaDesde = Integer.parseInt(res.horaInicio.split(":")[0]);
                int horaHasta = Integer.parseInt(res.horaFin.split(":")[0]);
                for (int h = horaDesde; h < horaHasta; h++) {
                    matriz.get(res.idRecurso).put(h, new Celda(res.actividad, res.idFuncionario));
                }
            } catch (Exception ignorado) {
                // Hora con formato inesperado; se ignora esa reserva puntual.
            }
        }
        return matriz;
    }
}