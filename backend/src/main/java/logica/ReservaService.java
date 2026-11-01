package logica;

import datos.RecursoDao;
import datos.ReservaDao;
import modelo.Recurso;
import modelo.Reserva;
import modelo.ResultadoReserva;
import modelo.ValidacionException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReservaService {

    private final ReservaDao reservaDao = new ReservaDao();
    private final RecursoDao recursoDao = new RecursoDao();

    public List<Reserva> misReservas(String funcionarioId) throws SQLException {
        return reservaDao.listarPorFuncionario(funcionarioId);
    }

    // synchronized: ahora hay VARIOS Frontend conectados a la vez. Si dos piden el
    // mismo recurso al mismo tiempo, solo uno debe ganarlo. Con synchronized el
    // segundo espera a que el primero termine de guardar.
    public synchronized ResultadoReserva crearReserva(String funcionarioId, String actividad, String textoFecha,
                                                      String textoHoraInicio, String textoHoraFin,
                                                      List<String> categoriasIds) throws ValidacionException, SQLException {
        LocalDate fecha = Validaciones.fecha(textoFecha, "Fecha inválida. Use el formato aaaa-mm-dd.");
        LocalTime horaInicio = Validaciones.hora(textoHoraInicio, "Hora inválida. Use el formato hh:mm.");
        LocalTime horaFin = Validaciones.hora(textoHoraFin, "Hora inválida. Use el formato hh:mm.");
        validarDatosBasicos(funcionarioId, actividad, fecha, horaInicio, horaFin, categoriasIds);

        // Recursos ya ocupados en ese horario (lo calcula MySQL)
        Set<String> ocupados = reservaDao.recursosOcupados(fecha, horaInicio, horaFin);

        List<String> recursosAsignados = new ArrayList<>();
        List<String> categoriasNoDisponibles = new ArrayList<>();
        for (String categoriaId : categoriasIds) {
            Recurso libre = null;
            for (Recurso r : recursoDao.listarPorCategoria(categoriaId)) {
                if (!ocupados.contains(r.getId()) && !recursosAsignados.contains(r.getId())) {
                    libre = r;
                    break;
                }
            }
            if (libre == null) categoriasNoDisponibles.add(categoriaId);
            else recursosAsignados.add(libre.getId());
        }

        if (!categoriasNoDisponibles.isEmpty()) return ResultadoReserva.fallo(categoriasNoDisponibles);

        Reserva reserva = new Reserva();
        reserva.setId(String.format("RES-%06d", reservaDao.ultimoNumero() + 1));
        reserva.setFuncionarioId(funcionarioId);
        reserva.setActividad(actividad.trim());
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setCategoriasIds(new ArrayList<>(categoriasIds));
        reserva.setRecursosIds(recursosAsignados);
        reserva.setEstado("ACTIVA");

        reservaDao.crear(reserva);
        return ResultadoReserva.exito(reserva);
    }

    public void cancelarReserva(String id, String funcionarioId) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("Seleccione una reserva.");
        Reserva reserva = reservaDao.buscarPorId(id);
        if (reserva == null) throw new ValidacionException("No se encontró la reserva con id: " + id);
        if (!reserva.getFuncionarioId().equalsIgnoreCase(funcionarioId))
            throw new ValidacionException("No puede cancelar una reserva de otro funcionario.");
        if ("CANCELADA".equals(reserva.getEstado()))
            throw new ValidacionException("Esa reserva ya está cancelada.");
        if (reserva.getFecha().isBefore(LocalDate.now()))
            throw new ValidacionException("Solo se pueden cancelar reservas futuras.");
        reservaDao.actualizarEstado(id, "CANCELADA");
    }

    // Publico y estatico para poder probarlo con un test de unidad (sin BD).
    public static boolean seSolapan(LocalTime inicioA, LocalTime finA, LocalTime inicioB, LocalTime finB) {
        return inicioA.isBefore(finB) && inicioB.isBefore(finA);
    }

    void validarDatosBasicos(String funcionarioId, String actividad, LocalDate fecha, LocalTime horaInicio,
                             LocalTime horaFin, List<String> categoriasIds) throws ValidacionException {
        if (Validaciones.vacio(funcionarioId)) throw new ValidacionException("No hay un funcionario en sesión.");
        if (Validaciones.vacio(actividad)) throw new ValidacionException("La actividad es obligatoria.");
        if (fecha == null) throw new ValidacionException("Debe indicar la fecha.");
        if (horaInicio == null || horaFin == null) throw new ValidacionException("Debe indicar hora de inicio y hora de fin.");
        if (!horaInicio.isBefore(horaFin)) throw new ValidacionException("La hora de inicio debe ser antes que la hora de fin.");
        if (categoriasIds == null || categoriasIds.isEmpty()) throw new ValidacionException("Debe seleccionar al menos una categoría.");
    }
}