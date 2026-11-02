package datos;

import modelo.Funcionario;
import modelo.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDao {

    public List<Funcionario> listar() throws SQLException {
        String sql = "SELECT id, nombre, telefono FROM funcionario ORDER BY nombre";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Funcionario> lista = new ArrayList<>();
            while (rs.next()) lista.add(convertir(rs));
            return lista;
        }
    }

    // Busca por id exacto o por parte del nombre.
    public List<Funcionario> buscar(String texto) throws SQLException {
        String sql = "SELECT id, nombre, telefono FROM funcionario "
                + "WHERE id = ? OR LOWER(nombre) LIKE ? ORDER BY nombre";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, texto);
            ps.setString(2, "%" + texto.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Funcionario> lista = new ArrayList<>();
                while (rs.next()) lista.add(convertir(rs));
                return lista;
            }
        }
    }

    public Funcionario buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, nombre, telefono FROM funcionario WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? convertir(rs) : null;
            }
        }
    }

    // Crea el usuario (para el login) y el funcionario EN UNA SOLA TRANSACCION:
    // o se guardan los dos, o no se guarda ninguno.
    public void crear(Funcionario f, String clave) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (id, clave, rol) VALUES (?, ?, ?)";
        String sqlFuncionario = "INSERT INTO funcionario (id, nombre, telefono) VALUES (?, ?, ?)";
        try (Connection cn = Database.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement psU = cn.prepareStatement(sqlUsuario);
                 PreparedStatement psF = cn.prepareStatement(sqlFuncionario)) {
                psU.setString(1, f.getId());
                psU.setString(2, clave);
                psU.setString(3, Rol.FUNCIONARIO.name());
                psU.executeUpdate();

                psF.setString(1, f.getId());
                psF.setString(2, f.getNombre());
                psF.setString(3, f.getTelefono());
                psF.executeUpdate();

                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    public int modificar(String id, String nombre, String telefono) throws SQLException {
        String sql = "UPDATE funcionario SET nombre = ?, telefono = ? WHERE id = ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, id);
            return ps.executeUpdate();
        }
    }

    // Borra el USUARIO; la fila de "funcionario" se borra sola por el ON DELETE CASCADE.
    // Si el funcionario tiene reservas, Oracle lo impide (FOREIGN KEY) y lanza excepcion.
    public int eliminar(String id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ? AND rol = 'FUNCIONARIO'";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate();
        }
    }

    private Funcionario convertir(ResultSet rs) throws SQLException {
        // La clave NO se manda al Frontend: se deja vacia.
        // (En Oracle un texto vacio '' se guarda como NULL, por eso el telefono puede venir null.)
        return new Funcionario(rs.getString("id"), "", Rol.FUNCIONARIO,
                rs.getString("nombre"), rs.getString("telefono") == null ? "" : rs.getString("telefono"));
    }
}