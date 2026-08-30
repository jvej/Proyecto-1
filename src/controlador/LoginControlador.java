package controlador;
import dao.UsuarioDAO;
import modelo.Usuario;
import util.Sesion;

public class LoginControlador {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String id, String clave) {
        Usuario u = usuarioDAO.buscarPorId(id);
        if (u != null && u.getClave().equals(clave)) {
            Sesion.getInstancia().setUsuarioActual(u);
            return u;
        }
        return null;
    }

    public boolean cambiarClave(String id, String claveActual, String claveNueva) {
        return usuarioDAO.cambiarClave(id, claveActual, claveNueva);
    }
}