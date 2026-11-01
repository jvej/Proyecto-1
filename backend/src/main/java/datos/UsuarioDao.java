package datos;

import modelo.Rol;
import modelo.Usuario;

import java.sql.*;

// DAO = Data Access Object. Aqui (y solo aqui) va el SQL de la tabla "usuario".
public class UsuarioDao {

    public Usuario buscar(String id) throws SQLException {
        String sql = "SELECT id, clave, rol FROM usuario WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getString("id"), rs.getString("clave"), Rol.valueOf(rs.getString("rol")));
                }
                return null;
            }
        }
    }

    public boolean existe(String id) throws SQLException {
        return buscar(id) != null;
    }

    public int actualizarClave(String id, String claveNueva) throws SQLException {
        String sql = "UPDATE usuario SET clave = ? WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, claveNueva);
            ps.setString(2, id);
            return ps.executeUpdate();
        }
    }
}