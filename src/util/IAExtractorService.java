package util;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IAExtractorService {

    private static final String MODELO = "gemini-3.6-flash";
    private static final String URL_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

    public DatosExtraidos extraer(String frase, List<String> categoriasDisponibles) throws Exception {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("No se encontró la variable de entorno GEMINI_API_KEY.");
        }

        String prompt = construirPrompt(frase, categoriasDisponibles);
        String cuerpoRequest = construirCuerpoRequest(prompt);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + MODELO + ":generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpoRequest))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al llamar a Gemini (código " + response.statusCode() + "): " + response.body());
        }

        String textoGenerado = extraerTextoDeRespuesta(response.body());
        return parsearDatos(textoGenerado);
    }

    // Armado del "prompt" (sin cambios)

    private String construirPrompt(String frase, List<String> categoriasDisponibles) {
        StringBuilder sb = new StringBuilder();
        sb.append("Extrae los datos de una reserva de recursos a partir de esta frase en lenguaje natural.\n");
        sb.append("Frase: \"").append(frase).append("\"\n\n");
        sb.append("Categorías de recursos disponibles (elegí SOLO entre estas, escritas EXACTAMENTE igual):\n");
        for (String categoria : categoriasDisponibles) {
            sb.append("- ").append(categoria).append("\n");
        }
        sb.append("\nHoy es: ").append(LocalDate.now()).append("\n\n");
        sb.append("Responde ÚNICAMENTE con un JSON válido, sin texto adicional ni marcadores de código, ");
        sb.append("con exactamente este formato:\n");
        sb.append("{\"actividad\": \"...\", \"fecha\": \"aaaa-mm-dd\", \"horaInicio\": \"hh:mm\", ");
        sb.append("\"horaFin\": \"hh:mm\", \"categorias\": [\"...\", \"...\"]}");
        return sb.toString();
    }

    // Armar el JSON de salida a mano

    private String construirCuerpoRequest(String prompt) {
        String promptEscapado = escaparJson(prompt);
        return "{\"contents\":[{\"parts\":[{\"text\":\"" + promptEscapado + "\"}]}]}";
    }

    private String escaparJson(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }

    //Leer el JSON de entrada con regex

    // Busca "text": "..." (el texto generado por Gemini) dentro de la respuesta completa.
    private String extraerTextoDeRespuesta(String jsonRespuesta) {
        String textoEscapado = extraerCampoTexto(jsonRespuesta, "text");
        if (textoEscapado == null) {
            throw new RuntimeException("No se pudo leer la respuesta de Gemini: " + jsonRespuesta);
        }
        return desescaparJson(textoEscapado);
    }

    private DatosExtraidos parsearDatos(String textoJson) {
        String limpio = textoJson.trim();
        if (limpio.startsWith("```")) {
            limpio = limpio.replace("```json", "").replace("```", "").trim();
        }

        DatosExtraidos datos = new DatosExtraidos();
        datos.setActividad(valorODefecto(extraerCampoTexto(limpio, "actividad")));
        datos.setFecha(valorODefecto(extraerCampoTexto(limpio, "fecha")));
        datos.setHoraInicio(valorODefecto(extraerCampoTexto(limpio, "horaInicio")));
        datos.setHoraFin(valorODefecto(extraerCampoTexto(limpio, "horaFin")));
        datos.setCategoriasDescripciones(extraerArregloTexto(limpio, "categorias"));

        return datos;
    }

    // Captura el valor de "nombreCampo": "valor" — incluyendo comillas/backslashes escapados dentro del valor.
    private String extraerCampoTexto(String json, String nombreCampo) {
        Pattern patron = Pattern.compile("\"" + nombreCampo + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher m = patron.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    // Captura el arreglo "nombreCampo": ["a", "b", ...] y devuelve cada elemento ya limpio.
    private List<String> extraerArregloTexto(String json, String nombreCampo) {
        List<String> resultado = new ArrayList<>();
        Pattern patronArreglo = Pattern.compile("\"" + nombreCampo + "\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
        Matcher mArreglo = patronArreglo.matcher(json);
        if (!mArreglo.find()) return resultado;

        String contenido = mArreglo.group(1);
        Pattern patronElemento = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher mElemento = patronElemento.matcher(contenido);
        while (mElemento.find()) {
            resultado.add(desescaparJson(mElemento.group(1)));
        }
        return resultado;
    }

    // Convierte \" -> " , \\ -> \ , \n -> salto de línea real, etc.
    private String desescaparJson(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' && i + 1 < texto.length()) {
                char siguiente = texto.charAt(i + 1);
                switch (siguiente) {
                    case '"':  sb.append('"');  i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case 'n':  sb.append('\n'); i++; break;
                    case 't':  sb.append('\t'); i++; break;
                    default:   sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String valorODefecto(String valor) {
        return valor == null ? "" : valor;
    }
}
