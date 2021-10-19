package lt.terzer.sql.filters;

import java.util.List;

public interface UsernameFilterable<T> {
    default T getByUsername(String username) {
        return getByUsernames(List.of(username)).stream().findFirst().orElse(null);
    }
    List<T> getByUsernames(List<String> names);
}
