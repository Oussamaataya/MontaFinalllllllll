package Services;

import java.util.List;

public interface IService <T>{
    //crud
    int add(T t);
    void update(T t);
    void delete(T t);
     List<T> getAll();
     T getOne( int id );
}
