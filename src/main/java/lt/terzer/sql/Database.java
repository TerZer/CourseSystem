package lt.terzer.sql;

import java.util.Arrays;
import java.util.List;

public interface Database<T>{

    default boolean save(T... obj){
        if(obj == null)
            return true;
        else
            return save(Arrays.asList(obj));
    }
    boolean save(List<T> obj);
    default boolean remove(T... obj){
        if(obj == null)
            return true;
        else
            return remove(Arrays.asList(obj));
    }
    boolean remove(List<T> obj);
    void shutdown();
    List<T> getAll();
    DatabaseStatus status();
}
