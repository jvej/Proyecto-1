package util;

import modelo.Recurso;
import modelo.Reserva;
import modelo.ValidacionException;
import persistence.RecursoXMLPersistence;
import persistence.ReservaXMLPersistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {

    private final ReservaXMLPersistence reservaPersistence = new ReservaXMLPersistence();
    private final RecursoXMLPersistence recursoPersistence = new RecursoXMLPersistence();

    /** Resultado de intentar crear una reserva: éxito con la reserva creada, o fallo con las categorías sin disponibilidad. */
    public static class ResultadoReserva {
        private boolean exito;
        private Reserva reserva;
        private List<String> categoriasNoDisponibles;

        public static ResultadoReserva exito(Reserva reserva) {
            ResultadoReserva r = new ResultadoReserva();
            r.exito = true;
            r.reserva = reserva;
            return r;
        }

        public static ResultadoReserva fallo(List<String> categoriasNoDisponibles) {
            ResultadoReserva r = new ResultadoReserva();
            r.exito = false;
            r.categoriasNoDisponibles = categoriasNoDisponibles;
            return r;
        }

        public boolean isExito() { return exito; }
        public Reserva getReserva() { return reserva; }
        public List<String> getCategoriasNoDisponibles() { return categoriasNoDisponibles; }
    }

    public List<Reserva> misReservas(String funcionarioId) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservaPersistence.readAll()) {
            if (r.getFuncionarioId().equalsIgnoreCase(funcionarioId)) resultado.add(r);
        }
        return resultado;
    }

    public ResultadoReserva crearReserva(String funcionarioId, String actividad, LocalDate fecha,
                                         LocalTime horaInicio, LocalTime horaFin,
                                         List<String> categoriasIds) throws ValidacionException {
        validarDatosBasicos(actividad, fecha, horaInicio, horaFin, categoriasIds);

        List<Reserva> todasLasReservas = reservaPersistence.readAll();
        List<Reserva> reservasDelDia = new ArrayList<>();
        for (Reserva r : todasLasReservas) {
            if (r.getFecha().equals(fecha) && "ACTIVA".equals(r.getEstado())) reservasDelDia.add(r);
        }
        List<Recurso> todosLosRecursos = recursoPersistence.readAll();

        List<String> recursosAsignados = new ArrayList<>();
        List<String> categoriasNoDisponibles = new ArrayList<>();
        for (String categoriaId : categoriasIds) {
            Recurso disponible = buscarPrimerDisponible(categoriaId, horaInicio, horaFin,
                    reservasDelDia, todosLosRecursos, recursosAsignados);
            if (disponible == null) categoriasNoDisponibles.add(categoriaId);
            else recursosAsignados.add(disponible.getId());
        }

        if (!categoriasNoDisponibles.isEmpty()) return ResultadoReserva.fallo(categoriasNoDisponibles);

        Reserva reserva = new Reserva();
        reserva.setId(generarNuevoId(todasLasReservas));
        reserva.setFuncionarioId(funcionarioId);
        reserva.setActividad(actividad.trim());
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setCategoriasIds(categoriasIds);
        reserva.setRecursosIds(recursosAsignados);
        reserva.setEstado("ACTIVA");

        todasLasReservas.add(reserva);
        reservaPersistence.writeAll(todasLasReservas);
        return ResultadoReserva.exito(reserva);
    }

    public void cancelarReserva(String id, String funcionarioId) throws ValidacionException {
        List<Reserva> lista = reservaPersistence.readAll();
        for (Reserva reserva : lista) {
            if (reserva.getId().equalsIgnoreCase(id)) {
                if (!reserva.getFuncionarioId().equalsIgnoreCase(funcionarioId))
                    throw new ValidacionException("No puede cancelar una reserva de otro funcionario.");
                if ("CANCELADA".equals(reserva.getEstado()))
                    throw new ValidacionException("Esa reserva ya está cancelada.");
                if (reserva.getFecha().isBefore(LocalDate.now()))
                    throw new ValidacionException("Solo se pueden cancelar reservas futuras.");
                reserva.setEstado("CANCELADA");
                reservaPersistence.writeAll(lista);
                return;
            }
        }
        throw new ValidacionException("No se encontró la reserva con id: " + id);
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

    private String generarNuevoId(List<Reserva> reservas) {
        int max = 0;
        for (Reserva r : reservas) {
            try { max = Math.max(max, Integer.parseInt(r.getId().replace("RES-", ""))); }
            catch (NumberFormatException ignorado) { /* id con otro formato, se ignora */ }
        }
        return String.format("RES-%06d", max + 1);
    }

    private void validarDatosBasicos(String actividad, LocalDate fecha, LocalTime horaInicio,
                                     LocalTime horaFin, List<String> categoriasIds) throws ValidacionException {
        if (actividad == null || actividad.trim().isEmpty()) throw new ValidacionException("La actividad es obligatoria.");
        if (fecha == null) throw new ValidacionException("Debe indicar la fecha.");
        if (horaInicio == null || horaFin == null) throw new ValidacionException("Debe indicar hora de inicio y hora de fin.");
        if (!horaInicio.isBefore(horaFin)) throw new ValidacionException("La hora de inicio debe ser antes que la hora de fin.");
        if (categoriasIds == null || categoriasIds.isEmpty()) throw new ValidacionException("Debe seleccionar al menos una categoría.");
    }
}