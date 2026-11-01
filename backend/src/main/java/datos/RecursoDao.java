package datos;

import modelo.Recurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDao {

    public List<Recurso> listar() throws SQLException {
        return buscar(null, null);
    }

    // Filtros opcionales: si vienen null o vacios no se aplican.
    public List<Recurso> buscar(String categoriaId, String descripcion) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, categoria_id, descripcion FROM recurso WHERE 1 = 1");
        List<String> parametros = new ArrayList<>();
        if (categoriaId != null && !categoriaId.isBlank()) {
            sql.append(" AND categoria_id = ?");
            parametros.add(categoriaId);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            sql.append(" AND LOWER(descripcion) LIKE ?");
            parametros.add("%" + descripcion.toLowerCase() + "%");
        }
        sql.append(" ORDER BY id");

        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) ps.setString(i + 1, parametros.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Recurso> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(new Recurso(rs.getString("id"), rs.getString("categoria_id"), rs.getString("descripcion")));
                }
                return lista;
            }
        }
    }

    public List<Recurso> listarPorCategoria(String categoriaId) throws SQLException {
        return buscar(categoriaId, null);
    }

    public Recurso buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, categoria_id, descripcion FROM recurso WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? new Recurso(rs.getString("id"), rs.getString("categoria_id"), rs.getString("descripcion"))
                        : null;
            }
        }
    }

    // Si el id no existe lo inserta; si ya existe, lo actualiza ("upsert").
    public void guardar(Recurso r) throws SQLException {
        String sql = "INSERT INTO recurso (id, categoria_id, descripcion) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE categoria_id = VALUES(categoria_id), descripcion = VALUES(descripcion)";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getCategoriaId());
            ps.setString(3, r.getDescripcion());
            ps.executeUpdate();
        }
    }

    public int eliminar(String id) throws SQLException {
        String sql = "DELETE FROM recurso WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate();
        }
    }
}