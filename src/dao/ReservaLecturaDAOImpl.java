package dao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.XMLManager;

import java.util.ArrayList;
import java.util.List;

public class ReservaLecturaDAOImpl implements ReservaLecturaDAO {

    private static final String RUTA = "data/reservas.xml";

    @Override
    public List<ReservaInfo> listarPorFecha(String fecha, List<String> idsRecurso) {
        List<ReservaInfo> lista = new ArrayList<>();
        Document doc = XMLManager.cargarODCrear(RUTA, "reservas");
        NodeList nodosReserva = doc.getElementsByTagName("reserva");

        for (int i = 0; i < nodosReserva.getLength(); i++) {
            Element reservaEl = (Element) nodosReserva.item(i);

            String fechaReserva = XMLManager.getTexto(reservaEl, "fecha");
            if (!fechaReserva.equals(fecha)) continue;

            String actividad = XMLManager.getTexto(reservaEl, "actividad");
            String horaInicio = XMLManager.getTexto(reservaEl, "horaInicio");
            String horaFin = XMLManager.getTexto(reservaEl, "horaFin");
            String idFuncionario = XMLManager.getTexto(reservaEl, "idFuncionario");

            NodeList nodosRecurso = reservaEl.getElementsByTagName("recurso");
            for (int j = 0; j < nodosRecurso.getLength(); j++) {
                String idRecurso = nodosRecurso.item(j).getTextContent();
                if (idsRecurso.contains(idRecurso)) {
                    lista.add(new ReservaInfo(idRecurso, horaInicio, horaFin, actividad, idFuncionario));
                }
            }
        }
        return lista;
    }
}