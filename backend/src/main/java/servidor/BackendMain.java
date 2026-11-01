package servidor;

import protocolo.Protocolo;

// Punto de entrada del BACKEND. No tiene interfaz grafica.
// Uso:  java -jar backend.jar [puerto]      (por defecto 1234)
public class BackendMain {
    public static void main(String[] args) throws Exception {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : Protocolo.PUERTO;
        System.out.println("Base de datos: " + datos.Database.getUrl());
        new Servidor(puerto).run();
    }
}