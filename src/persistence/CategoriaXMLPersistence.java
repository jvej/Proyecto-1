package persistence;

import modelo.Categoria;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class CategoriaXMLPersistence implements XMLPersistence<Categoria> {

    private static final String RUTA = "data/categorias.xml";

    @Override
    public List<Categoria> readAll() {
        List<Categoria> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "categorias");
        NodeList nodos = doc.getElementsByTagName("categoria");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            lista.add(new Categoria(XMLManager.getTexto(el, "id"), XMLManager.getTexto(el, "descripcion")));
        }
        return lista;
    }

    @Override
    public void writeAll(List<Categoria> objects) {
        Document doc = XMLManager.cargarODCrear(RUTA, "categorias");
        Element raiz = doc.getDocumentElement();
        while (raiz.hasChildNodes()) raiz.removeChild(raiz.getFirstChild());

        for (Categoria c : objects) {
            Element nuevo = doc.createElement("categoria");
            XMLManager.crearHijoTexto(doc, nuevo, "id", c.getId());
            XMLManager.crearHijoTexto(doc, nuevo, "descripcion", c.getDescripcion());
            raiz.appendChild(nuevo);
        }
        XMLManager.guardar(doc, RUTA);
    }
}