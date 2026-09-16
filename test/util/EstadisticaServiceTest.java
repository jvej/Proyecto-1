package util;

import modelo.ValidacionException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// EstadisticaService lee "data/reservas.xml" y "data/categorias.xml" (rutas
// fijas), asi que respaldamos ambos archivos antes de cada prueba que
// necesita datos y los restauramos despues, mismo patron que ReservaServiceTest.
class EstadisticaServiceTest {

    private static final Path RUTA_RESERVAS = Path.of("data/reservas.xml");
    private static final Path RUTA_CATEGORIAS = Path.of("data/categorias.xml");
    private String reservasOriginal;
    private String categoriasOriginal;

    @BeforeEach
    void respaldar() throws IOException {
        reservasOriginal = Files.exists(RUTA_RESERVAS) ? Files.readString(RUTA_RESERVAS) : null;
        categoriasOriginal = Files.exists(RUTA_CATEGORIAS) ? Files.readString(RUTA_CATEGORIAS) : null;
    }

    @AfterEach
    void restaurar() throws IOException {
        restaurarArchivo(RUTA_RESERVAS, reservasOriginal);
        restaurarArchivo(RUTA_CATEGORIAS, categoriasOriginal);
    }

    private void restaurarArchivo(Path ruta, String contenido) throws IOException {
        if (contenido != null) Files.writeString(ruta, contenido);
        else if (Files.exists(ruta)) Files.delete(ruta);
    }

    @Test
    void validarRangoConDesdeMayorQueHastaLanzaExcepcion() {
        EstadisticaService service = new EstadisticaService();
        assertThrows(ValidacionException.class, () -> service.validarRango("2026-09-30", "2026-09-01"));
    }

    @Test
    void validarRangoConFechasValidasDevuelveDesdeYHastaEnOrden() throws ValidacionException {
        EstadisticaService service = new EstadisticaService();
        LocalDate[] rango = service.validarRango("2026-09-01", "2026-09-30");
        assertEquals(LocalDate.of(2026, 9, 1), rango[0]);
        assertEquals(LocalDate.of(2026, 9, 30), rango[1]);
    }

    @Test
    void estadisticasRecursosPorCategoriaCuentaCadaCategoriaSolicitada() throws Exception {
        Files.writeString(RUTA_CATEGORIAS, """
                <?xml version="1.0" encoding="UTF-8"?>
                <categorias>
                    <categoria><id>CAT-TEST</id><descripcion>Sala de Pruebas</descripcion></categoria>
                </categorias>
                """);
        Files.writeString(RUTA_RESERVAS, """
                <?xml version="1.0" encoding="UTF-8"?>
                <reservas>
                    <reserva>
                        <id>RES-A</id><funcionarioId>111</funcionarioId><actividad>A</actividad>
                        <fecha>2026-09-01</fecha><horaInicio>08:00</horaInicio><horaFin>09:00</horaFin><estado>ACTIVA</estado>
                        <categoriasIds><categoriaId>CAT-TEST</categoriaId></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                    <reserva>
                        <id>RES-B</id><funcionarioId>222</funcionarioId><actividad>B</actividad>
                        <fecha>2026-09-02</fecha><horaInicio>08:00</horaInicio><horaFin>09:00</horaFin><estado>ACTIVA</estado>
                        <categoriasIds><categoriaId>CAT-TEST</categoriaId></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                </reservas>
                """);

        EstadisticaService service = new EstadisticaService();
        Map<String, Integer> resultado = service.estadisticasRecursosPorCategoria("2026-09-01", "2026-09-30");

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get("Sala de Pruebas"));
    }

    @Test
    void estadisticasActividadesPorSemanaAgrupaPorElLunesDeCadaSemana() throws Exception {
        Files.writeString(RUTA_RESERVAS, """
                <?xml version="1.0" encoding="UTF-8"?>
                <reservas>
                    <reserva>
                        <id>RES-C</id><funcionarioId>111</funcionarioId><actividad>C</actividad>
                        <fecha>2026-08-10</fecha><horaInicio>08:00</horaInicio><horaFin>09:00</horaFin><estado>ACTIVA</estado>
                        <categoriasIds></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                    <reserva>
                        <id>RES-D</id><funcionarioId>222</funcionarioId><actividad>D</actividad>
                        <fecha>2026-08-12</fecha><horaInicio>08:00</horaInicio><horaFin>09:00</horaFin><estado>ACTIVA</estado>
                        <categoriasIds></categoriasIds><recursosIds></recursosIds>
                    </reserva>
                </reservas>
                """);

        EstadisticaService service = new EstadisticaService();
        Map<LocalDate, Integer> resultado = service.estadisticasActividadesPorSemana("2026-08-01", "2026-08-31");

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(LocalDate.of(2026, 8, 10)));
    }
}