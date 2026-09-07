package dao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;

import java.util.ArrayList;
import java.util.List;

public class RecursoLecturaDAOImpl implements RecursoLecturaDAO {

    private static final String RUTA = "data/recursos.xml";

    @Override
    public List<RecursoInfo> listarPorCategoria(String idCategoria) {
        List<RecursoInfo> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "recursos");
        NodeList nodos = doc.getElementsByTagName("recurso");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            String cat = XMLManager.getTexto(el, "idCategoria");
            if (cat.equalsIgnoreCase(idCategoria)) {
                lista.add(new RecursoInfo(XMLManager.getTexto(el, "id"), cat, XMLManager.getTexto(el, "descripcion")));
            }
        }
        return lista;
    }
}