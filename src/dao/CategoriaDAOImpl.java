package dao;

import modelo.Categoria;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    private static final String RUTA = "data/categorias.xml";

    @Override
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "categorias");
        NodeList nodos = doc.getElementsByTagName("categoria");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            lista.add(new Categoria(
                    XMLManager.getTexto(el, "id"),
                    XMLManager.getTexto(el, "descripcion")
            ));
        }
        return lista;
    }

    @Override
    public Categoria buscarPorId(String id) {
        for (Categoria c : listar()) {
            if (c.getId().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    @Override
    public void guardar(Categoria categoria) {
        Document doc = XMLManager.cargarODCrear(RUTA, "categorias");
        Element raiz = doc.getDocumentElement();

        // Si es nueva (sin id), se autogenera antes de guardar.
        if (categoria.getId() == null || categoria.getId().trim().isEmpty()) {
            categoria.setId(generarSiguienteId());
        } else {
            // Si ya existe, se remueve para reemplazarlo (upsert simple).
            NodeList nodos = doc.getElementsByTagName("categoria");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element el = (Element) nodos.item(i);
                if (XMLManager.getTexto(el, "id").equalsIgnoreCase(categoria.getId())) {
                    raiz.removeChild(el);
                    break;
                }
            }
        }

        Element nuevo = doc.createElement("categoria");
        XMLManager.crearHijoTexto(doc, nuevo, "id", categoria.getId());
        XMLManager.crearHijoTexto(doc, nuevo, "descripcion", categoria.getDescripcion());
        raiz.appendChild(nuevo);

        XMLManager.guardar(doc, RUTA);
    }

    @Override
    public void eliminar(String id) {
        Document doc = XMLManager.cargarODCrear(RUTA, "categorias");
        Element raiz = doc.getDocumentElement();
        NodeList nodos = doc.getElementsByTagName("categoria");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (XMLManager.getTexto(el, "id").equalsIgnoreCase(id)) {
                raiz.removeChild(el);
                XMLManager.guardar(doc, RUTA);
                return;
            }
        }
    }

    /** Genera el siguiente id secuencial: CAT-000001, CAT-000002, ... */
    private String generarSiguienteId() {
        int maximo = 0;
        for (Categoria c : listar()) {
            try {
                int numero = Integer.parseInt(c.getId().replace("CAT-", ""));
                if (numero > maximo) maximo = numero;
            } catch (NumberFormatException ignorado) {
                // Id con otro formato (cargado a mano); se ignora para el conteo.
            }
        }
        return String.format("CAT-%06d", maximo + 1);
    }
}