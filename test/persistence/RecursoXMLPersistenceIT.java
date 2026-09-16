package persistence;

import modelo.Categoria;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Prueba de INTEGRACIÓN: valida que CategoriaXMLPersistence lea y escriba
// correctamente contra el archivo XML real, no contra datos en memoria.
// Mismo patrón que RecursoXMLPersistenceIT.
class CategoriaXMLPersistenceIT {

    private static final Path RUTA = Path.of("data/categorias.xml");
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
    void escribirYLeerCategoriaDesdeArchivoReal() {
        CategoriaXMLPersistence persistence = new CategoriaXMLPersistence();

        List<Categoria> lista = persistence.readAll();
        lista.add(new Categoria("CAT-IT-001", "Categoria de integracion"));
        persistence.writeAll(lista);

        List<Categoria> releido = persistence.readAll();
        assertTrue(releido.stream().anyMatch(c ->
                c.getId().equals("CAT-IT-001") && c.getDescripcion().equals("Categoria de integracion")));
    }

    @Test
    void writeAllSobreescribeCompletamenteElArchivoAnterior() {
        CategoriaXMLPersistence persistence = new CategoriaXMLPersistence();

        persistence.writeAll(List.of(new Categoria("CAT-IT-A", "Primera")));
        persistence.writeAll(List.of(new Categoria("CAT-IT-B", "Segunda")));

        List<Categoria> releido = persistence.readAll();
        assertEquals(1, releido.size());
        assertEquals("CAT-IT-B", releido.get(0).getId());
    }
}