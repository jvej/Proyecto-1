package datos;

import modelo.Reserva;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ReservaDao {

    private static final String SELECT_BASE =
            "SELECT id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado FROM reserva ";

    public List<Reserva> listarPorFuncionario(String funcionarioId) throws SQLException {
        return consultar(SELECT_BASE + "WHERE funcionario_id = ? ORDER BY fecha DESC, hora_inicio", funcionarioId);
    }

    // Reservas ACTIVAS entre dos fechas (inclusive). Se usa en Calendarizacion y Actividades.
    public List<Reserva> listarActivasEntre(LocalDate desde, LocalDate hasta) throws SQLException {
        return consultar(SELECT_BASE + "WHERE estado = 'ACTIVA' AND fecha BETWEEN ? AND ? ORDER BY fecha, hora_inicio",
                desde, hasta);
    }

    public Reserva buscarPorId(String id) throws SQLException {
        List<Reserva> lista = consultar(SELECT_BASE + "WHERE id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Ids de los recursos que YA estan ocupados en esa fecha por una reserva activa
    // que se traslapa con el horario [inicio, fin).
    // Dos horarios se traslapan si:  inicioA < finB  Y  inicioB < finA
    public Set<String> recursosOcupados(LocalDate fecha, LocalTime inicio, LocalTime fin) throws SQLException {
        String sql = "SELECT rr.recurso_id FROM reserva r "
                + "JOIN reserva_recurso rr ON rr.reserva_id = r.id "
                + "WHERE r.estado = 'ACTIVA' AND r.fecha = ? AND r.hora_inicio < ? AND ? < r.hora_fin";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, fecha);
            ps.setObject(2, fin);
            ps.setObject(3, inicio);
            try (ResultSet rs = ps.executeQuery()) {
                Set<String> ocupados = new HashSet<>();
                while (rs.next()) ocupados.add(rs.getString(1));
                return ocupados;
            }
        }
    }

    // Inserta la reserva y sus categorias/recursos en UNA transaccion.
    public void crear(Reserva r) throws SQLException {
        String sqlReserva = "INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCategoria = "INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES (?, ?)";
        String sqlRecurso = "INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (?, ?)";

        try (Connection cn = Database.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sqlReserva);
                 PreparedStatement psC = cn.prepareStatement(sqlCategoria);
                 PreparedStatement psR = cn.prepareStatement(sqlRecurso)) {
                ps.setString(1, r.getId());
                ps.setString(2, r.getFuncionarioId());
                ps.setString(3, r.getActividad());
                ps.setObject(4, r.getFecha());
                ps.setObject(5, r.getHoraInicio());
                ps.setObject(6, r.getHoraFin());
                ps.setString(7, r.getEstado());
                ps.executeUpdate();

                for (String categoriaId : new LinkedHashSet<>(r.getCategoriasIds())) {
                    psC.setString(1, r.getId());
                    psC.setString(2, categoriaId);
                    psC.executeUpdate();
                }
                for (String recursoId : new LinkedHashSet<>(r.getRecursosIds())) {
                    psR.setString(1, r.getId());
                    psR.setString(2, recursoId);
                    psR.executeUpdate();
                }
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    public int actualizarEstado(String id, String estado) throws SQLException {
        String sql = "UPDATE reserva SET estado = ? WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, id);
            return ps.executeUpdate();
        }
    }

    public int ultimoNumero() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(id, 5) AS UNSIGNED)), 0) FROM reserva WHERE id LIKE 'RES-%'";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ---------- Estadisticas (el conteo lo hace MySQL con GROUP BY) ----------

    // Cuantas veces se pidio cada categoria en reservas activas del rango.
    public Map<String, Integer> contarPorCategoria(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT c.descripcion, COUNT(*) AS cantidad "
                + "FROM reserva r "
                + "JOIN reserva_categoria rc ON rc.reserva_id = r.id "
                + "JOIN categoria c ON c.id = rc.categoria_id "
                + "WHERE r.estado = 'ACTIVA' AND r.fecha BETWEEN ? AND ? "
                + "GROUP BY c.descripcion";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Integer> conteo = new TreeMap<>();
                while (rs.next()) conteo.put(rs.getString("descripcion"), rs.getInt("cantidad"));
                return conteo;
            }
        }
    }

    // Cuantas reservas activas hubo por semana. La semana se identifica por su lunes:
    // WEEKDAY(fecha) da 0 para lunes, 1 martes... entonces fecha - WEEKDAY(fecha) = lunes.
    public Map<LocalDate, Integer> contarPorSemana(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT DATE_SUB(fecha, INTERVAL WEEKDAY(fecha) DAY) AS lunes, COUNT(*) AS cantidad "
                + "FROM reserva "
                + "WHERE estado = 'ACTIVA' AND fecha BETWEEN ? AND ? "
                + "GROUP BY lunes";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                Map<LocalDate, Integer> conteo = new TreeMap<>();
                while (rs.next()) conteo.put(rs.getObject("lunes", LocalDate.class), rs.getInt("cantidad"));
                return conteo;
            }
        }
    }

    // ---------- Helpers ----------

    private List<Reserva> consultar(String sql, Object... parametros) throws SQLException {
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
            List<Reserva> lista = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(convertir(rs));
            }
            for (Reserva r : lista) cargarDetalle(cn, r);
            return lista;
        }
    }

    private Reserva convertir(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setId(rs.getString("id"));
        r.setFuncionarioId(rs.getString("funcionario_id"));
        r.setActividad(rs.getString("actividad"));
        r.setFecha(rs.getObject("fecha", LocalDate.class));
        r.setHoraInicio(rs.getObject("hora_inicio", LocalTime.class));
        r.setHoraFin(rs.getObject("hora_fin", LocalTime.class));
        r.setEstado(rs.getString("estado"));
        return r;
    }

    // Llena las listas de categorias y recursos de la reserva (tablas intermedias).
    private void cargarDetalle(Connection cn, Reserva r) throws SQLException {
        r.setCategoriasIds(listaDeIds(cn,
                "SELECT categoria_id FROM reserva_categoria WHERE reserva_id = ? ORDER BY categoria_id", r.getId()));
        r.setRecursosIds(listaDeIds(cn,
                "SELECT recurso_id FROM reserva_recurso WHERE reserva_id = ? ORDER BY recurso_id", r.getId()));
    }

    private List<String> listaDeIds(Connection cn, String sql, String reservaId) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getString(1));
                return ids;
            }
        }
    }
}