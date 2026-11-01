package logica;

import datos.FuncionarioDao;
import datos.UsuarioDao;
import modelo.Funcionario;
import modelo.Rol;
import modelo.ValidacionException;

import java.sql.SQLException;
import java.util.List;

public class FuncionarioService {

    private final FuncionarioDao dao = new FuncionarioDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();

    public List<Funcionario> listar() throws SQLException { return dao.listar(); }

    public List<Funcionario> buscar(String texto) throws SQLException {
        if (Validaciones.vacio(texto)) return listar();
        return dao.buscar(texto.trim());
    }

    public void crear(String id, String nombre, String telefono, String clave) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("El ID es obligatorio.");
        if (Validaciones.vacio(nombre)) throw new ValidacionException("El nombre es obligatorio.");
        if (Validaciones.vacio(clave)) throw new ValidacionException("La clave es obligatoria.");
        validarTelefono(telefono);

        String idLimpio = id.trim();
        if (usuarioDao.existe(idLimpio)) throw new ValidacionException("Ya existe un usuario con ese ID.");

        dao.crear(new Funcionario(idLimpio, "", Rol.FUNCIONARIO, nombre.trim(),
                telefono == null ? "" : telefono.trim()), clave.trim());
    }

    public void modificar(String id, String nombre, String telefono) throws ValidacionException, SQLException {
        if (Validaciones.vacio(nombre)) throw new ValidacionException("El nombre es obligatorio.");
        validarTelefono(telefono);
        int filas = dao.modificar(id.trim(), nombre.trim(), telefono == null ? "" : telefono.trim());
        if (filas == 0) throw new ValidacionException("No existe un funcionario con ese ID.");
    }

    // Si ya existe lo modifica; si no, lo crea (igual que en el Proyecto #1).
    public void guardar(String id, String nombre, String telefono, String clave) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("El ID es obligatorio.");
        if (dao.buscarPorId(id.trim()) != null) modificar(id, nombre, telefono);
        else crear(id, nombre, telefono, clave);
    }

    public void eliminar(String id) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("Seleccione un funcionario de la lista.");
        try {
            int filas = dao.eliminar(id.trim());
            if (filas == 0) throw new ValidacionException("No se encontró el funcionario con id: " + id);
        } catch (SQLException e) {
            if (Validaciones.esViolacionDeLlave(e)) {
                throw new ValidacionException("No se puede eliminar: el funcionario tiene reservas registradas.");
            }
            throw e;
        }
    }

    private void validarTelefono(String telefono) throws ValidacionException {
        if (!Validaciones.vacio(telefono) && !telefono.trim().matches("[0-9 +()-]{4,20}")) {
            throw new ValidacionException("Teléfono inválido: use solo números (ej: 88888888).");
        }
    }
}