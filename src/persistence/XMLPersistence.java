package persistence;

import java.util.List;

public interface XMLPersistence<T> {
    List<T> readAll();
    void writeAll(List<T> objects);
}