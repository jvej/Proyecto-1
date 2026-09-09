package persistence;

import modelo.Funcionario;
import modelo.Rol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioXMLPersistence implements XMLPersistence<Funcionario> {

    private static final String RUTA = "data/funcionarios.xml";

    @Override
    public List<Funcionario> readAll() {
        List<Funcionario> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "funcionarios");
        NodeList nodos = doc.getElementsByTagName("funcionario");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            lista.add(new Funcionario(
                    XMLManager.getTexto(el, "id"),
                    XMLManager.getTexto(el, "clave"),
                    Rol.FUNCIONARIO,
                    XMLManager.getTexto(el, "nombre"),
                    XMLManager.getTexto(el, "telefono")
            ));
        }
        return lista;
    }

    @Override
    public void writeAll(List<Funcionario> objects) {
        Document doc = XMLManager.cargarODCrear(RUTA, "funcionarios");
        Element raiz = doc.getDocumentElement();
        while (raiz.hasChildNodes()) raiz.removeChild(raiz.getFirstChild());

        for (Funcionario f : objects) {
            Element nuevo = doc.createElement("funcionario");
            XMLManager.crearHijoTexto(doc, nuevo, "id", f.getId());
            XMLManager.crearHijoTexto(doc, nuevo, "clave", f.getClave());
            XMLManager.crearHijoTexto(doc, nuevo, "nombre", f.getNombre());
            XMLManager.crearHijoTexto(doc, nuevo, "telefono", f.getTelefono());
            raiz.appendChild(nuevo);
        }
        XMLManager.guardar(doc, RUTA);
    }
}