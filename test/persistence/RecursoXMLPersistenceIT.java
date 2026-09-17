package persistence;

import modelo.Recurso;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecursoXMLPersistenceIT {

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
    void escribirYLeerRecursoDesdeArchivoReal() {
        RecursoXMLPersistence persistence = new RecursoXMLPersistence();

        List<Recurso> lista = persistence.readAll();
        lista.add(new Recurso("IT-001", "CAT-TEST", "Recurso de integración"));
        persistence.writeAll(lista);

        List<Recurso> releido = persistence.readAll();
        assertTrue(releido.stream().anyMatch(r ->
                r.getId().equals("IT-001") && r.getCategoriaId().equals("CAT-TEST")));
    }
}