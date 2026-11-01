package protocolo;

// Constantes que Frontend y Backend usan para "hablar el mismo idioma".
// Cada peticion empieza con un numero (int) que dice QUE se quiere hacer;
// luego van los parametros. La respuesta empieza con un codigo de error.
public final class Protocolo {
    private Protocolo() { }

    // ---- Configuracion por defecto ----
    public static final String SERVIDOR = "localhost";
    public static final int PUERTO = 1234;

    // ---- Tipo de conexion (lo primero que manda el Frontend al conectarse) ----
    public static final int SYNC = 1;   // socket de peticion/respuesta
    public static final int ASYNC = 2;  // socket por donde llegan las notificaciones

    // ---- Codigos de respuesta ----
    public static final int ERROR_NO_ERROR = 0;   // todo bien, sigue el resultado
    public static final int ERROR_VALIDACION = 1; // error de negocio, sigue el mensaje (String)
    public static final int ERROR_ERROR = 2;      // error inesperado, sigue el mensaje (String)

    // ---- Notificacion asincronica (Backend -> Frontend) ----
    public static final int DELIVER_ALERTA = 100;

    // ---- Peticiones (Frontend -> Backend) ----
    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int CAMBIAR_CLAVE = 3;
    public static final int DESCONECTAR = 4;

    public static final int FUNCIONARIO_LISTAR = 10;
    public static final int FUNCIONARIO_BUSCAR = 11;
    public static final int FUNCIONARIO_GUARDAR = 12;
    public static final int FUNCIONARIO_ELIMINAR = 13;

    public static final int CATEGORIA_LISTAR = 20;
    public static final int CATEGORIA_BUSCAR = 21;
    public static final int CATEGORIA_GUARDAR = 22;
    public static final int CATEGORIA_ELIMINAR = 23;

    public static final int RECURSO_LISTAR = 30;
    public static final int RECURSO_BUSCAR = 31;
    public static final int RECURSO_GUARDAR = 32;
    public static final int RECURSO_BORRAR = 33;

    public static final int RESERVA_MIS_RESERVAS = 40;
    public static final int RESERVA_CREAR = 41;
    public static final int RESERVA_CANCELAR = 42;

    public static final int CALENDARIZACION_RECURSOS = 50;
    public static final int CALENDARIZACION_MATRIZ = 51;

    public static final int ACTIVIDAD_MATRIZ_SEMANA = 60;

    public static final int ESTADISTICA_RECURSOS = 70;
    public static final int ESTADISTICA_ACTIVIDADES = 71;
}