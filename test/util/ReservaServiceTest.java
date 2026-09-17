package util;

import modelo.ValidacionException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    private static final Path RUTA_RECURSOS = Path.of("data/recursos.xml");
    private static final Path RUTA_RESERVAS = Path.of("data/reservas.xml");
    private String recursosOriginal;
    private String reservasOriginal;

    @BeforeEach
    void respaldar() throws IOException {
        recursosOriginal = Files.exists(RUTA_RECURSOS) ? Files.readString(RUTA_RECURSOS) : null;
        reservasOriginal = Files.exists(RUTA_RESERVAS) ? Files.readString(RUTA_RESERVAS) : null;
    }

    @AfterEach
    void restaurar() throws IOException {
        restaurarArchivo(RUTA_RECURSOS, recursosOriginal);
        restaurarArchivo(RUTA_RESERVAS, reservasOriginal);
    }

    private void restaurarArchivo(Path ruta, String contenido) throws IOException {
        if (contenido != null) Files.writeString(ruta, contenido);
        else if (Files.exists(ruta)) Files.delete(ruta);
    }

    @Test
    void crearReservaSinActividadLanzaExcepcion() {
        ReservaService service = new ReservaService();
        assertThrows(ValidacionException.class, () -> service.crearReserva(
                "111", "", LocalDate.now().plusDays(1).toString(),
                LocalTime.of(9, 0).toString(), LocalTime.of(10, 0).toString(), List.of("CAT-TEST")));
    }

    @Test
    void crearReservaConHorarioInvalidoLanzaExcepcion() {
        ReservaService service = new ReservaService();
        assertThrows(ValidacionException.class, () -> service.crearReserva(
                "111", "Reunion", LocalDate.now().plusDays(1).toString(),
                LocalTime.of(10, 0).toString(), LocalTime.of(9, 0).toString(), List.of("CAT-TEST")));
    }

    @Test
    void crearReservaConRecursoDisponibleTieneExito() throws Exception {
        new RecursoService().guardar("TEST-R1", "CAT-TEST", "Recurso de prueba");

        ReservaService.ResultadoReserva resultado = new ReservaService().crearReserva(
                "111", "Reunion de prueba", LocalDate.now().plusDays(1).toString(),
                LocalTime.of(9, 0).toString(), LocalTime.of(10, 0).toString(), List.of("CAT-TEST"));

        assertTrue(resultado.isExito());
        assertEquals("TEST-R1", resultado.getReserva().getRecursosIds().get(0));
    }

    @Test
    void segundaReservaEnMismoHorarioSinRecursoLibreFalla() throws Exception {
        new RecursoService().guardar("TEST-R1", "CAT-TEST", "Recurso de prueba");
        ReservaService service = new ReservaService();
        LocalDate fecha = LocalDate.now().plusDays(2);
        String textoFecha = fecha.toString();

        service.crearReserva("111", "Primera reunion", textoFecha,
                LocalTime.of(9, 0).toString(), LocalTime.of(10, 0).toString(), List.of("CAT-TEST"));

        ReservaService.ResultadoReserva resultado = service.crearReserva(
                "222", "Segunda reunion", textoFecha,
                LocalTime.of(9, 30).toString(), LocalTime.of(10, 30).toString(), List.of("CAT-TEST"));

        assertFalse(resultado.isExito());
        assertTrue(resultado.getCategoriasNoDisponibles().contains("CAT-TEST"));
    }

    @Test
    void cancelarReservaDeOtroFuncionarioLanzaExcepcion() throws Exception {
        new RecursoService().guardar("TEST-R1", "CAT-TEST", "Recurso de prueba");
        ReservaService service = new ReservaService();
        var resultado = service.crearReserva("111", "Reunion", LocalDate.now().plusDays(3).toString(),
                LocalTime.of(9, 0).toString(), LocalTime.of(10, 0).toString(), List.of("CAT-TEST"));

        String idReserva = resultado.getReserva().getId();
        assertThrows(ValidacionException.class, () -> service.cancelarReserva(idReserva, "222"));
    }
}