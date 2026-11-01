package datos;

import modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    public List<Categoria> listar() throws SQLException {
        String sql = "SELECT id, descripcion FROM categoria ORDER BY id";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Categoria> lista = new ArrayList<>();
            while (rs.next()) lista.add(new Categoria(rs.getString("id"), rs.getString("descripcion")));
            return lista;
        }
    }

    public List<Categoria> buscar(String texto) throws SQLException {
        String sql = "SELECT id, descripcion FROM categoria WHERE LOWER(descripcion) LIKE ? ORDER BY id";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Categoria> lista = new ArrayList<>();
                while (rs.next()) lista.add(new Categoria(rs.getString("id"), rs.getString("descripcion")));
                return lista;
            }
        }
    }

    public Categoria buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, descripcion FROM categoria WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Categoria(rs.getString("id"), rs.getString("descripcion")) : null;
            }
        }
    }

    public void crear(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria (id, descripcion) VALUES (?, ?)";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getDescripcion());
            ps.executeUpdate();
        }
    }

    public int modificar(String id, String descripcion) throws SQLException {
        String sql = "UPDATE categoria SET descripcion = ? WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setString(2, id);
            return ps.executeUpdate();
        }
    }

    public int eliminar(String id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate();
        }
    }

    // Mayor numero usado en los ids "CAT-000123" (0 si no hay ninguno).
    public int ultimoNumero() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(id, 5) AS UNSIGNED)), 0) FROM categoria WHERE id LIKE 'CAT-%'";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}