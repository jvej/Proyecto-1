package persistence;

import modelo.Reserva;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaXMLPersistence implements XMLPersistence<Reserva> {

    private static final String RUTA = "data/reservas.xml";

    @Override
    public List<Reserva> readAll() {
        List<Reserva> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "reservas");
        NodeList nodos = doc.getElementsByTagName("reserva");
        for (int i = 0; i < nodos.getLength(); i++) {
            lista.add(convertirAObjeto((Element) nodos.item(i)));
        }
        return lista;
    }

    @Override
    public void writeAll(List<Reserva> objects) {
        Document doc = XMLManager.cargarODCrear(RUTA, "reservas");
        Element raiz = doc.getDocumentElement();
        while (raiz.hasChildNodes()) raiz.removeChild(raiz.getFirstChild());

        for (Reserva r : objects) {
            raiz.appendChild(convertirAElemento(doc, r));
        }
        XMLManager.guardar(doc, RUTA);
    }

    private Reserva convertirAObjeto(Element el) {
        Reserva r = new Reserva();
        r.setId(XMLManager.getTexto(el, "id"));
        r.setFuncionarioId(XMLManager.getTexto(el, "funcionarioId"));
        r.setActividad(XMLManager.getTexto(el, "actividad"));
        r.setFecha(LocalDate.parse(XMLManager.getTexto(el, "fecha")));
        r.setHoraInicio(LocalTime.parse(XMLManager.getTexto(el, "horaInicio")));
        r.setHoraFin(LocalTime.parse(XMLManager.getTexto(el, "horaFin")));
        r.setEstado(XMLManager.getTexto(el, "estado"));
        r.setCategoriasIds(leerLista(el, "categoriaId"));
        r.setRecursosIds(leerLista(el, "recursoId"));
        return r;
    }

    private Element convertirAElemento(Document doc, Reserva r) {
        Element el = doc.createElement("reserva");
        XMLManager.crearHijoTexto(doc, el, "id", r.getId());
        XMLManager.crearHijoTexto(doc, el, "funcionarioId", r.getFuncionarioId());
        XMLManager.crearHijoTexto(doc, el, "actividad", r.getActividad());
        XMLManager.crearHijoTexto(doc, el, "fecha", r.getFecha().toString());
        XMLManager.crearHijoTexto(doc, el, "horaInicio", r.getHoraInicio().toString());
        XMLManager.crearHijoTexto(doc, el, "horaFin", r.getHoraFin().toString());
        XMLManager.crearHijoTexto(doc, el, "estado", r.getEstado());

        Element categorias = doc.createElement("categoriasIds");
        for (String categoriaId : r.getCategoriasIds()) {
            XMLManager.crearHijoTexto(doc, categorias, "categoriaId", categoriaId);
        }
        el.appendChild(categorias);

        Element recursos = doc.createElement("recursosIds");
        for (String recursoId : r.getRecursosIds()) {
            XMLManager.crearHijoTexto(doc, recursos, "recursoId", recursoId);
        }
        el.appendChild(recursos);
        return el;
    }

    private List<String> leerLista(Element padre, String tagHijo) {
        List<String> lista = new ArrayList<>();
        NodeList nodos = padre.getElementsByTagName(tagHijo);
        for (int i = 0; i < nodos.getLength(); i++) lista.add(nodos.item(i).getTextContent());
        return lista;
    }
}