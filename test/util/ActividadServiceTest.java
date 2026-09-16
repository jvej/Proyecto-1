package util;

import modelo.ValidacionException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActividadServiceTest {

    private static final Path RUTA_RESERVAS = Path.of("data/reservas.xml");
    private String contenidoOriginal;

    @BeforeEach
    void respaldar() throws IOException {
        contenidoOriginal = Files.exists(RUTA_RESERVAS) ? Files.readString(RUTA_RESERVAS) : null;
    }

    @AfterEach
    void restaurar() throws IOException {
        if (contenidoOriginal != null) Files.writeString(RUTA_RESERVAS, contenidoOriginal);
        else if (Files.exists(RUTA_RESERVAS)) Files.delete(RUTA_RESERVAS);
    }

    @Test
    void validarFechaConFormatoInvalidoLanzaExcepcion() {
        ActividadService service = new ActividadService();
        assertThrows(ValidacionException.class, () -> service.validarFecha("14/08/2026"));
    }

    @Test
    void validarFechaConFormatoValidoDevuelveLaFechaCorrecta() throws ValidacionException {
        ActividadService service = new ActividadService();
        assertEquals(LocalDate.of(2026, 8, 14), service.validarFecha("2026-08-14"));
    }

    @Test
    void calcularLunesDeSemanaSiempreDevuelveUnLunes() {
        ActividadService service = new ActividadService();
        LocalDate miercoles = LocalDate.of(2026, 8, 12);
        LocalDate lunesEsperado = LocalDate.of(2026, 8, 10);

        assertEquals(lunesEsperado, service.calcularLunesDeSemana(miercoles));
        assertEquals(lunesEsperado, service.calcularLunesDeSemana(lunesEsperado));
    }

    @Test
    void obtenerMatrizSemanaIncluyeUnaReservaActivaEnElDiaYHoraCorrectos() throws Exception {
        Files.writeString(RUTA_RESERVAS, """
                <?xml version="1.0" encoding="UTF-8"?>
                <reservas>
                    <reserva>
                        <id>RES-TEST-1</id><funcionarioId>111</funcionarioId><actividad>Prueba unitaria</actividad>
                        <fecha>2026-08-12</fecha><horaInicio>14:00</horaInicio><horaFin>16:00</horaFin><estado>ACTIVA</estado>
                        <categoriasIds></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                </reservas>
                """);

        ActividadService service = new ActividadService();
        Map<LocalDate, Map<Integer, ActividadService.Celda>> matriz = service.obtenerMatrizSemana("2026-08-12");

        ActividadService.Celda celda = matriz.get(LocalDate.of(2026, 8, 12)).get(14);
        assertNotNull(celda);
        assertEquals("Prueba unitaria", celda.actividad);
        assertEquals("111", celda.idFuncionario);
    }

    @Test
    void obtenerMatrizSemanaNoIncluyeReservasCanceladas() throws Exception {
        Files.writeString(RUTA_RESERVAS, """
                <?xml version="1.0" encoding="UTF-8"?>
                <reservas>
                    <reserva>
                        <id>RES-TEST-2</id><funcionarioId>111</funcionarioId><actividad>No deberia aparecer</actividad>
                        <fecha>2026-08-13</fecha><horaInicio>10:00</horaInicio><horaFin>11:00</horaFin><estado>CANCELADA</estado>
                        <categoriasIds></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                </reservas>
                """);

        ActividadService service = new ActividadService();
        Map<LocalDate, Map<Integer, ActividadService.Celda>> matriz = service.obtenerMatrizSemana("2026-08-12");

        assertNull(matriz.get(LocalDate.of(2026, 8, 13)).get(10));
    }

    @Test
    void obtenerMatrizSemanaNoIncluyeReservasDeOtraSemana() throws Exception {
        Files.writeString(RUTA_RESERVAS, """
                <?xml version="1.0" encoding="UTF-8"?>
                <reservas>
                    <reserva>
                        <id>RES-TEST-3</id><funcionarioId>111</funcionarioId><actividad>Semana equivocada</actividad>
                        <fecha>2026-01-01</fecha><horaInicio>08:00</horaInicio><horaFin>09:00</horaFin><estado>ACTIVA</estado>
                        <categoriasIds></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                </reservas>
                """);

        ActividadService service = new ActividadService();
        Map<LocalDate, Map<Integer, ActividadService.Celda>> matriz = service.obtenerMatrizSemana("2026-08-12");

        for (Map<Integer, ActividadService.Celda> columna : matriz.values()) {
            assertTrue(columna.isEmpty());
        }
    }
}