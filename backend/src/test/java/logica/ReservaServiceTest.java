package logica;

import modelo.ValidacionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests de UNIDAD: no necesitan MySQL porque la validacion ocurre ANTES de ir a la BD.
class ReservaServiceTest {

    private final ReservaService service = new ReservaService();
    private final String manana = LocalDate.now().plusDays(1).toString();

    @Test
    void sinActividadLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> service.crearReserva("111", "", manana, "09:00", "10:00", List.of("CAT-1")));
    }

    @Test
    void horaInicioDespuesDeHoraFinLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> service.crearReserva("111", "Reunion", manana, "10:00", "09:00", List.of("CAT-1")));
    }

    @Test
    void fechaConFormatoMaloLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> service.crearReserva("111", "Reunion", "02/11/2026", "09:00", "10:00", List.of("CAT-1")));
    }

    @Test
    void sinCategoriasLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> service.crearReserva("111", "Reunion", manana, "09:00", "10:00", List.of()));
    }

    @Test
    void horariosQueSeTraslapan() {
        assertTrue(ReservaService.seSolapan(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(9, 30), LocalTime.of(11, 0)));
        assertFalse(ReservaService.seSolapan(LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)));
    }
}