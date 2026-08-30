package util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLManager {

    public static Document cargarODCrear(String rutaArchivo, String nombreRaiz) {
        try {
            File archivo = new File(rutaArchivo);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (archivo.exists()) {
                return builder.parse(archivo);
            } else {
                Document doc = builder.newDocument();
                Element raiz = doc.createElement(nombreRaiz);
                doc.appendChild(raiz);
                guardar(doc, rutaArchivo);
                return doc;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando XML: " + rutaArchivo, e);
        }
    }

    public static void guardar(Document doc, String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(archivo));
        } catch (Exception e) {
            throw new RuntimeException("Error guardando XML: " + rutaArchivo, e);
        }
    }

    public static String getTexto(Element padre, String tag) {
        NodeList lista = padre.getElementsByTagName(tag);
        if (lista.getLength() == 0 || lista.item(0).getTextContent() == null) return "";
        return lista.item(0).getTextContent();
    }

    public static Element crearHijoTexto(Document doc, Element padre, String tag, String valor) {
        Element el = doc.createElement(tag);
        el.setTextContent(valor);
        padre.appendChild(el);
        return el;
    }
}