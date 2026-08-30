package dao;
import modelo.Rol;
import modelo.Usuario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String RUTA = "data/usuarios.xml";

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "usuarios");
        NodeList nodos = doc.getElementsByTagName("usuario");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            Usuario u = new Usuario(
                    XMLManager.getTexto(el, "id"),
                    XMLManager.getTexto(el, "clave"),
                    Rol.valueOf(XMLManager.getTexto(el, "rol"))
            );
            lista.add(u);
        }
        return lista;
    }

    public Usuario buscarPorId(String id) {
        for (Usuario u : listar()) {
            if (u.getId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    public void guardar(Usuario usuario) {
        Document doc = XMLManager.cargarODCrear(RUTA, "usuarios");
        Element raiz = doc.getDocumentElement();

        NodeList nodos = doc.getElementsByTagName("usuario");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (XMLManager.getTexto(el, "id").equalsIgnoreCase(usuario.getId())) {
                raiz.removeChild(el);
                break;
            }
        }

        Element nuevo = doc.createElement("usuario");
        XMLManager.crearHijoTexto(doc, nuevo, "id", usuario.getId());
        XMLManager.crearHijoTexto(doc, nuevo, "clave", usuario.getClave());
        XMLManager.crearHijoTexto(doc, nuevo, "rol", usuario.getRol().name());
        raiz.appendChild(nuevo);

        XMLManager.guardar(doc, RUTA);
    }

    public boolean cambiarClave(String id, String claveActual, String claveNueva) {
        Usuario u = buscarPorId(id);
        if (u == null || !u.getClave().equals(claveActual)) return false;
        u.setClave(claveNueva);
        guardar(u);
        return true;
    }
}