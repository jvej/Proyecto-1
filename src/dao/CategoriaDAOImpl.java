package dao;
//package com.reservas.dao;

//import com.reservas.database.GestorPersistenciaXML;
//import com.reservas.model.Categoria;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
public class CategoriaDAOImpl implements CategoriaDAO {
    private static final String ARCHIVO = "categorias.xml";
    private static final String RAIZ = "categorias";
    private static final String NODO = "categoria";

    @Override
    public List<Categoria> listarTodos() throws PersistenciaException {
        try {
            List<Categoria> lista = new ArrayList<>();
            Document doc = GestorPersistenciaXML.cargarDocumento(ARCHIVO, RAIZ);
            NodeList nodos = doc.getElementsByTagName(NODO);

            for (int i = 0; i < nodos.getLength(); i++) {
                Element el = (Element) nodos.item(i);
                lista.add(new Categoria(GestorPersistenciaXML.obtenerTexto(el, "id"), GestorPersistenciaXML.obtenerTexto(el, "descripcion")));
            }
            return lista;

        } catch (Exception e) {
            throw new PersistenciaException("Error al leer " + ARCHIVO, e);
        }
    }
    @Override
    public Categoria buscarPorId(String id) throws PersistenciaException {
        for (Categoria c : listarTodos()) {
            if (c.getId().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    @Override
    public void insertar(Categoria categoria) throws PersistenciaException {
        categoria.setId(generarSiguienteId());
        guardarNodo(categoria);
    }

    @Override
    public void actualizar(Categoria categoria) throws PersistenciaException {
        guardarNodo(categoria);
    }

    @Override
    public void eliminar(String id) throws PersistenciaException {
        try {
            Document doc = GestorPersistenciaXML.cargarDocumento(ARCHIVO, RAIZ);
            Element raiz = doc.getDocumentElement();
            NodeList nodos = doc.getElementsByTagName(NODO);

            for (int i = 0; i < nodos.getLength(); i++) {
                Element el = (Element) nodos.item(i);
                if (GestorPersistenciaXML.obtenerTexto(el, "id").equalsIgnoreCase(id)) {
                    raiz.removeChild(el);
                    GestorPersistenciaXML.guardarDocumento(doc, ARCHIVO);
                    return;
                }
            }
        } catch (Exception e) {
            throw new PersistenciaException("Error al borrar de " + ARCHIVO, e);
        }
    }

    private String generarSiguienteId() throws PersistenciaException {
        int maximo = 0;
        for (Categoria c : listarTodos()) {
            try {
                int numero = Integer.parseInt(c.getId().replace("CAT-", ""));
                if (numero > maximo) maximo = numero;
            } catch (NumberFormatException ignorado) {
            }
        }
        return String.format("CAT-%06d", maximo + 1);
    }

    private void guardarNodo(Categoria c) throws PersistenciaException {
        try {
            Document doc = GestorPersistenciaXML.cargarDocumento(ARCHIVO, RAIZ);
            Element raiz = doc.getDocumentElement();

            NodeList nodos = doc.getElementsByTagName(NODO);
            for (int i = 0; i < nodos.getLength(); i++) {
                Element el = (Element) nodos.item(i);
                if (GestorPersistenciaXML.obtenerTexto(el, "id").equalsIgnoreCase(c.getId())) {
                    raiz.removeChild(el);
                    break;
                }
            }

            Element nuevo = doc.createElement(NODO);
            GestorPersistenciaXML.crearHijoTexto(doc, nuevo, "id", c.getId());
            GestorPersistenciaXML.crearHijoTexto(doc, nuevo, "descripcion", c.getDescripcion());
            raiz.appendChild(nuevo);

            GestorPersistenciaXML.guardarDocumento(doc, ARCHIVO);

        } catch (Exception e) {
            throw new PersistenciaException("Error al guardar " + ARCHIVO, e);
        }
    }
}
