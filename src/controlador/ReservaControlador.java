package controlador;
import dao.RecursoDAO;
import dao.ReservaDAO;
import modelo.Recurso;
import modelo.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaControlador {
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final RecursoDAO recursoDAO = new RecursoDAO();

    public List<Reserva> misReservas(String funcionarioId) {
        return reservaDAO.listarPorFuncionario(funcionarioId);
    }
    public ResultadoReserva crearReserva(String funcionarioId, String actividad, LocalDate fecha,
                                         LocalTime horaInicio, LocalTime horaFin,
                                         List<String> categoriasIds) {
        validarDatosBasicos(actividad, fecha, horaInicio, horaFin, categoriasIds);

        List<Reserva> reservasDelDia = reservaDAO.listarActivasPorFecha(fecha);
        List<Recurso> todosLosRecursos = recursoDAO.listar();

        List<String> recursosAsignados = new ArrayList<>();
        List<String> categoriasNoDisponibles = new ArrayList<>();
        for (String categoriaId : categoriasIds) {
            Recurso disponible = buscarPrimerDisponible(categoriaId, horaInicio, horaFin,
                    reservasDelDia, todosLosRecursos, recursosAsignados);
            if (disponible == null) {
                categoriasNoDisponibles.add(categoriaId);
            } else {
                recursosAsignados.add(disponible.getId());
            }
        }

        if (!categoriasNoDisponibles.isEmpty()) {
            return ResultadoReserva.fallo(categoriasNoDisponibles);
        }
        Reserva reserva = new Reserva();
        reserva.setId(reservaDAO.generarNuevoId());
        reserva.setFuncionarioId(funcionarioId);
        reserva.setActividad(actividad.trim());
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setCategoriasIds(categoriasIds);
        reserva.setRecursosIds(recursosAsignados);
        reserva.setEstado("ACTIVA");

        reservaDAO.guardar(reserva);
        return ResultadoReserva.exito(reserva);
    }

    private Recurso buscarPrimerDisponible(String categoriaId, LocalTime horaInicio, LocalTime horaFin,
                                           List<Reserva> reservasDelDia, List<Recurso> todosLosRecursos,
                                           List<String> yaAsignados) {
        for (Recurso recurso : todosLosRecursos) {
            if (!recurso.getCategoriaId().equalsIgnoreCase(categoriaId)) continue;
            if (yaAsignados.contains(recurso.getId())) continue;
            if (estaLibre(recurso.getId(), horaInicio, horaFin, reservasDelDia)) return recurso;
        }
        return null;
    }

    private boolean estaLibre(String recursoId, LocalTime horaInicio, LocalTime horaFin, List<Reserva> reservasDelDia) {
        for (Reserva reserva : reservasDelDia) {
            if (reserva.getRecursosIds().contains(recursoId)
                    && seSolapan(horaInicio, horaFin, reserva.getHoraInicio(), reserva.getHoraFin())) {
                return false;
            }
        }
        return true;
    }

    private boolean seSolapan(LocalTime inicioA, LocalTime finA, LocalTime inicioB, LocalTime finB) {
        return inicioA.isBefore(finB) && inicioB.isBefore(finA);
    }

    public void cancelarReserva(String id, String funcionarioId) {
        Reserva reserva = reservaDAO.buscarPorId(id);
        if (reserva == null)
            throw new IllegalArgumentException("No se encontró la reserva con id: " + id);
        if (!reserva.getFuncionarioId().equalsIgnoreCase(funcionarioId))
            throw new IllegalArgumentException("No puede cancelar una reserva de otro funcionario.");
        if ("CANCELADA".equals(reserva.getEstado()))
            throw new IllegalArgumentException("Esa reserva ya está cancelada.");
        if (reserva.getFecha().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Solo se pueden cancelar reservas futuras.");
        reservaDAO.cancelar(id);
    }

    private void validarDatosBasicos(String actividad, LocalDate fecha, LocalTime horaInicio,
                                     LocalTime horaFin, List<String> categoriasIds) {
        if (actividad == null || actividad.trim().isEmpty())
            throw new IllegalArgumentException("La actividad es obligatoria.");
        if (fecha == null)
            throw new IllegalArgumentException("Debe indicar la fecha.");
        if (horaInicio == null || horaFin == null)
            throw new IllegalArgumentException("Debe indicar hora de inicio y hora de fin.");
        if (!horaInicio.isBefore(horaFin))
            throw new IllegalArgumentException("La hora de inicio debe ser antes que la hora de fin.");
        if (categoriasIds == null || categoriasIds.isEmpty())
            throw new IllegalArgumentException("Debe seleccionar al menos una categoría.");
    }
}
