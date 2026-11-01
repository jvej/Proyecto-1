package controlador;

import protocolo.IService;
import proxy.ServiceProxy;

// Los controladores NO saben que existe una base de datos ni un socket:
// solo conocen la interfaz IService. Aqui se decide que implementacion usar
// (el Proxy, que habla con el Backend por la red).
final class Remoto {
    private Remoto() { }

    static IService service() { return ServiceProxy.getInstancia(); }

    // Para metodos que la vista llama sin try/catch (listar, buscar):
    // convierte el error en RuntimeException; el manejador global de App.Main lo muestra.
    static RuntimeException error(Exception e) {
        return new RuntimeException(e.getMessage(), e);
    }
}