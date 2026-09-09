package persistence;

import modelo.Recurso;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class RecursoXMLPersistence implements XMLPersistence<Recurso> {

    private static final String RUTA = "data/recursos.xml";

    @Override
    public List<Recurso> readAll() {
        List<Recurso> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "recursos");
        NodeList nodos = doc.getElementsByTagName("recurso");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            lista.add(new Recurso(
                    XMLManager.getTexto(el, "id"),
                    XMLManager.getTexto(el, "idCategoria"),
                    XMLManager.getTexto(el, "descripcion")
            ));
        }
        return lista;
    }

    @Override
    public void writeAll(List<Recurso> objects) {
        Document doc = XMLManager.cargarODCrear(RUTA, "recursos");
        Element raiz = doc.getDocumentElement();
        while (raiz.hasChildNodes()) raiz.removeChild(raiz.getFirstChild());

        for (Recurso r : objects) {
            Element nuevo = doc.createElement("recurso");
            XMLManager.crearHijoTexto(doc, nuevo, "id", r.getId());
            XMLManager.crearHijoTexto(doc, nuevo, "idCategoria", r.getCategoriaId());
            XMLManager.crearHijoTexto(doc, nuevo, "descripcion", r.getDescripcion());
            raiz.appendChild(nuevo);
        }
        XMLManager.guardar(doc, RUTA);
    }
}