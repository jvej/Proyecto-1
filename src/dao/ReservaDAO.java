package dao;
import modelo.Reserva;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private static final String RUTA = "data/reservas.xml";

    public List<Reserva> listar() {
        List<Reserva> lista =new ArrayList<>();
        Document doc =XMLManager.cargarODCrear(RUTA, "reservas");
        NodeList nodos =doc.getElementsByTagName("reserva");

        for (int i = 0; i <nodos.getLength(); i++) {
            lista.add(convertirAObjeto((Element) nodos.item(i)));
        }
        return lista;
    }

    public Reserva buscarPorId(String id) {
        for (Reserva r : listar()) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public List<Reserva> listarPorFuncionario(String funcionarioId) {
        List<Reserva> resultado =new ArrayList<>();
        for (Reserva r : listar()) {
            if (r.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public List<Reserva> listarActivasPorFecha(LocalDate fecha) {
        List<Reserva> resultado =new ArrayList<>();
        for (Reserva r : listar()) {
            if (r.getFecha().equals(fecha) &&"ACTIVA".equals(r.getEstado())) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public String generarNuevoId() {
        int max = 0;
        for (Reserva r : listar()) {
            String numero = r.getId().replace("RES-", "");
            try {
                max = Math.max(max, Integer.parseInt(numero));
            } catch (NumberFormatException ignorado) {
                // si un id no sigue el formato esperado, se ignora
            }
        }
        return String.format("RES-%06d", max + 1);
    }

    public void guardar(Reserva reserva) {
        Document doc =XMLManager.cargarODCrear(RUTA, "reservas");
        Element raiz =doc.getDocumentElement();

        NodeList nodos =doc.getElementsByTagName("reserva");
        for (int i =0; i < nodos.getLength();i++) {
            Element el = (Element) nodos.item(i);
            if (XMLManager.getTexto(el, "id").equalsIgnoreCase(reserva.getId())) {
                raiz.removeChild(el);
                break;
            }
        }

        raiz.appendChild(convertirAElemento(doc, reserva));
        XMLManager.guardar(doc, RUTA);
    }

    public boolean cancelar(String id) {
        Reserva reserva =buscarPorId(id);
        if (reserva ==null) return false;
        reserva.setEstado("CANCELADA");
        guardar(reserva);
        return true;
    }

    // los puse aqui y no en XMLManager porque son especificas del formato de <reserva>,
    // no reglas genericas que todos los DAO deban compartir, hay que preguntarle al profe si no hay problema

    private Reserva convertirAObjeto(Element el) {
        Reserva r =new Reserva();
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
        Element el =doc.createElement("reserva");
        XMLManager.crearHijoTexto(doc, el, "id", r.getId());
        XMLManager.crearHijoTexto(doc, el, "funcionarioId", r.getFuncionarioId());
        XMLManager.crearHijoTexto(doc, el, "actividad", r.getActividad());
        XMLManager.crearHijoTexto(doc, el, "fecha", r.getFecha().toString());
        XMLManager.crearHijoTexto(doc, el, "horaInicio", r.getHoraInicio().toString());
        XMLManager.crearHijoTexto(doc, el, "horaFin", r.getHoraFin().toString());
        XMLManager.crearHijoTexto(doc, el, "estado", r.getEstado());

        Element categorias =doc.createElement("categoriasIds");
        for (String categoriaId : r.getCategoriasIds()) {
            XMLManager.crearHijoTexto(doc, categorias, "categoriaId", categoriaId);
        }
        el.appendChild(categorias);

        Element recursos =doc.createElement("recursosIds");
        for (String recursoId : r.getRecursosIds()) {
            XMLManager.crearHijoTexto(doc, recursos, "recursoId", recursoId);
        }
        el.appendChild(recursos);
        return el;
    }

    private List<String> leerLista(Element padre, String tagHijo) {
        List<String> lista =new ArrayList<>();
        NodeList nodos =padre.getElementsByTagName(tagHijo);
        for (int i =0; i <nodos.getLength();i++) {
            lista.add(nodos.item(i).getTextContent());
        }
        return lista;
    }
}
