package lt.terzer.sql.data;

public interface Savable {
    int getId();
    void setId(int id);
    boolean isDirty();
    void setDirty();
    void setDirty(boolean bool);
}
