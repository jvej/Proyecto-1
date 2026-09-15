package util;

import modelo.Recurso;
import modelo.ValidacionException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Como RecursoXMLPersistence siempre escribe en "data/recursos.xml" (ruta fija),
// respaldamos ese archivo antes de cada prueba y lo restauramos después,
// para no perder los datos reales de demo del equipo.
class RecursoServiceTest {

    private static final Path RUTA = Path.of("data/recursos.xml");
    private String contenidoOriginal;

    @BeforeEach
    void respaldar() throws IOException {
        contenidoOriginal = Files.exists(RUTA) ? Files.readString(RUTA) : null;
    }

    @AfterEach
    void restaurar() throws IOException {
        if (contenidoOriginal != null) Files.writeString(RUTA, contenidoOriginal);
        else if (Files.exists(RUTA)) Files.delete(RUTA);
    }

    @Test
    void guardarConIdVacioLanzaExcepcion() {
        RecursoService service = new RecursoService();
        assertThrows(ValidacionException.class,
                () -> service.guardar("", "CAT-000001", "Sala de prueba"));
    }

    @Test
    void guardarSinCategoriaLanzaExcepcion() {
        RecursoService service = new RecursoService();
        assertThrows(ValidacionException.class,
                () -> service.guardar("TEST-001", "", "Sala de prueba"));
    }

    @Test
    void guardarYListarFuncionaCorrectamente() throws Exception {
        RecursoService service = new RecursoService();
        service.guardar("TEST-001", "CAT-000001", "Recurso de prueba unitaria");

        List<Recurso> lista = service.listar();
        assertTrue(lista.stream().anyMatch(r -> r.getId().equals("TEST-001")));
    }

    @Test
    void borrarUnRecursoInexistenteLanzaExcepcion() {
        RecursoService service = new RecursoService();
        assertThrows(ValidacionException.class, () -> service.borrar("NO-EXISTE-999"));
    }
}