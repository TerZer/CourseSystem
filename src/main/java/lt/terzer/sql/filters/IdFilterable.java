package lt.terzer.sql.filters;

import java.util.List;

public interface IdFilterable<T> {
    default T getById(int id) {
        return getByIds(List.of(id)).stream().findFirst().orElse(null);
    }
    List<T> getByIds(List<Integer> ids);
}
