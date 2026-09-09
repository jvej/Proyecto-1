package persistence;

import modelo.Rol;
import modelo.Usuario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class UsuarioXMLPersistence implements XMLPersistence<Usuario> {

    private static final String RUTA = "data/usuarios.xml";

    @Override
    public List<Usuario> readAll() {
        List<Usuario> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "usuarios");
        NodeList nodos = doc.getElementsByTagName("usuario");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            lista.add(new Usuario(
                    XMLManager.getTexto(el, "id"),
                    XMLManager.getTexto(el, "clave"),
                    Rol.valueOf(XMLManager.getTexto(el, "rol"))
            ));
        }
        return lista;
    }

    @Override
    public void writeAll(List<Usuario> objects) {
        Document doc = XMLManager.cargarODCrear(RUTA, "usuarios");
        Element raiz = doc.getDocumentElement();
        while (raiz.hasChildNodes()) raiz.removeChild(raiz.getFirstChild());

        for (Usuario u : objects) {
            Element nuevo = doc.createElement("usuario");
            XMLManager.crearHijoTexto(doc, nuevo, "id", u.getId());
            XMLManager.crearHijoTexto(doc, nuevo, "clave", u.getClave());
            XMLManager.crearHijoTexto(doc, nuevo, "rol", u.getRol().name());
            raiz.appendChild(nuevo);
        }
        XMLManager.guardar(doc, RUTA);
    }
}