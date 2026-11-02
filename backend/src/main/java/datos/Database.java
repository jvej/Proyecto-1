package datos;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Unico lugar del sistema que sabe COMO conectarse a Oracle.
// Lee los datos de conexion del archivo "db.properties" (en la carpeta desde
// donde se corre el Backend). Si no existe, usa los valores por defecto.
//
// Cada metodo DAO pide una conexion nueva con getConnection() y la cierra al
// terminar (try-with-resources). Asi cada hilo (Worker) usa su propia conexion
// y no se estorban entre ellos.
public final class Database {

    private static String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private static String usuario = "reservas";
    private static String clave = "reservas";

    static {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (Exception noHayArchivo) {
            // Probamos dentro del jar / resources
            try (InputStream in = Database.class.getResourceAsStream("/db.properties")) {
                if (in != null) p.load(in);
            } catch (Exception ignorado) { }
        }
        url = p.getProperty("db.url", url);
        usuario = p.getProperty("db.usuario", usuario);
        clave = p.getProperty("db.clave", clave);
    }

    private Database() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    // Permite que los tests de integracion apunten a otra base si quieren.
    public static void configurar(String nuevaUrl, String nuevoUsuario, String nuevaClave) {
        url = nuevaUrl;
        usuario = nuevoUsuario;
        clave = nuevaClave;
    }

    public static String getUrl() { return url; }
}