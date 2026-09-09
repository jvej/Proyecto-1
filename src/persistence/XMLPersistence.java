package persistence;

import java.util.List;

//Contrato genérico de persistencia XML: leer todo / escribir todo
public interface XMLPersistence<T> {
    List<T> readAll();
    void writeAll(List<T> objects);
}