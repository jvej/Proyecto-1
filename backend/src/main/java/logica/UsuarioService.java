package logica;

import datos.UsuarioDao;
import modelo.Usuario;
import modelo.ValidacionException;

import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDao dao = new UsuarioDao();

    public Usuario autenticar(String id, String clave) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id) || Validaciones.vacio(clave)) {
            throw new ValidacionException("Debe completar el ID y la clave.");
        }
        Usuario u = dao.buscar(id.trim());
        if (u == null || !u.getClave().equals(clave)) {
            throw new ValidacionException("ID o clave incorrectos.");
        }
        u.setClave(""); // la clave no viaja de vuelta al Frontend
        return u;
    }

    public void cambiarClave(String id, String claveActual, String claveNueva, String confirmacion)
            throws ValidacionException, SQLException {
        if (Validaciones.vacio(id) || Validaciones.vacio(claveActual)
                || Validaciones.vacio(claveNueva) || Validaciones.vacio(confirmacion)) {
            throw new ValidacionException("Complete todos los campos.");
        }
        if (!claveNueva.equals(confirmacion)) {
            throw new ValidacionException("Las claves nuevas no coinciden.");
        }
        Usuario u = dao.buscar(id.trim());
        if (u == null || !u.getClave().equals(claveActual)) {
            throw new ValidacionException("ID o clave actual incorrectos.");
        }
        dao.actualizarClave(u.getId(), claveNueva);
    }
}