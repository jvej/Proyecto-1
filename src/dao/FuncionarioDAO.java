package dao;

import modelo.Funcionario;
import modelo.Rol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    private static final String RUTA = "data/funcionarios.xml";

    public List<Funcionario> listar() {
        List<Funcionario> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "funcionarios");
        NodeList nodos = doc.getElementsByTagName("funcionario");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            Funcionario f = new Funcionario(
                    XMLManager.getTexto(el, "id"),
                    XMLManager.getTexto(el, "clave"),
                    Rol.FUNCIONARIO,
                    XMLManager.getTexto(el, "nombre"),
                    XMLManager.getTexto(el, "telefono")
            );
            lista.add(f);
        }
        return lista;
    }

    public Funcionario buscarPorId(String id) {
        for (Funcionario f : listar()) {
            if (f.getId().equalsIgnoreCase(id)) return f;
        }
        return null;
    }

    public void guardar(Funcionario f) {
        Document doc = XMLManager.cargarODCrear(RUTA, "funcionarios");
        Element raiz = doc.getDocumentElement();

        NodeList nodos = doc.getElementsByTagName("funcionario");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (XMLManager.getTexto(el, "id").equalsIgnoreCase(f.getId())) {
                raiz.removeChild(el);
                break;
            }
        }

        Element nuevo = doc.createElement("funcionario");
        XMLManager.crearHijoTexto(doc, nuevo, "id", f.getId());
        XMLManager.crearHijoTexto(doc, nuevo, "clave", f.getClave());
        XMLManager.crearHijoTexto(doc, nuevo, "nombre", f.getNombre());
        XMLManager.crearHijoTexto(doc, nuevo, "telefono", f.getTelefono());
        raiz.appendChild(nuevo);

        XMLManager.guardar(doc, RUTA);
    }

    public boolean eliminar(String id) {
        Document doc = XMLManager.cargarODCrear(RUTA, "funcionarios");
        Element raiz = doc.getDocumentElement();
        NodeList nodos = doc.getElementsByTagName("funcionario");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (XMLManager.getTexto(el, "id").equalsIgnoreCase(id)) {
                raiz.removeChild(el);
                XMLManager.guardar(doc, RUTA);
                return true;
            }
        }
        return false;
    }
}