package dao;
//package com.reservas.dao;

//import com.reservas.model.Categoria;
import java.util.List;
public interface CategoriaDAO {
    List<Categoria> listarTodos() throws PersistenciaException;
    Categoria buscarPorId(String id) throws PersistenciaException;
    void insertar(Categoria categoria) throws PersistenciaException;
    void actualizar(Categoria categoria) throws PersistenciaException;
    void eliminar(String id) throws PersistenciaException;
}
