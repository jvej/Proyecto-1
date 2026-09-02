package dao;
import modelo.Recurso;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;

import java.util.ArrayList;
import java.util.List;

public class RecusoDAO {
    private static final String RUTA = "data/recursos.xml";

    public List<Recurso> listar(){
        List<Recurso> lista = new ArrayList<>();
        Document doc =XMLManager.cargarODCrear(RUTA, "recursos");
        NodeList nodos = doc.getElementsByTagName("recurso");

        for (int i = 0; i<nodos.getLength(); i++){
            Element el = (Element)nodos.item(i);
            Recurso r = new Recurso(
              XMLManager.getTexto(el, "id"),
              XMLManager.getTexto(el, "categoriaId"),
              XMLManager.getTexto(el, "descripcion") );
            lista.add(r);
        }
        return lista;
    }

    public Recurso buscarPorId(String id){
        for(Recurso r : listar()){
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public List<Recurso> buscar(String categoriaId, String descripcionParcial) {
        List<Recurso> resultado = new ArrayList<>();
        for (Recurso r : listar()) {
            boolean coincideCategoria = (categoriaId == null || categoriaId.isEmpty()
                    || r.getCategoriaId().equalsIgnoreCase(categoriaId));
            boolean coincideDescripcion = (descripcionParcial == null || descripcionParcial.isEmpty()
                    || r.getDescripcion().toLowerCase().contains(descripcionParcial.toLowerCase()));
            if (coincideCategoria && coincideDescripcion) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public void guardar(Recurso recurso) {
        Document doc = XMLManager.cargarODCrear(RUTA, "recursos");
        Element raiz = doc.getDocumentElement();

        NodeList nodos = doc.getElementsByTagName("recurso");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (XMLManager.getTexto(el, "id").equalsIgnoreCase(recurso.getId())) {
                raiz.removeChild(el);
                break;
            }
        }
        Element nuevo = doc.createElement("recurso");
        XMLManager.crearHijoTexto(doc, nuevo, "id", recurso.getId());
        XMLManager.crearHijoTexto(doc, nuevo, "categoriaId", recurso.getCategoriaId());
        XMLManager.crearHijoTexto(doc, nuevo, "descripcion", recurso.getDescripcion());
        raiz.appendChild(nuevo);

        XMLManager.guardar(doc, RUTA);
    }

    public boolean borrar(String id) {
        Document doc = XMLManager.cargarODCrear(RUTA, "recursos");
        Element raiz = doc.getDocumentElement();
        NodeList nodos = doc.getElementsByTagName("recurso");
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
