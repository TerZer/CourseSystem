package lt.terzer.sql.data;

public class DatabaseSavable implements Savable {

    protected int id = -1;
    protected boolean dirty = false;

    public DatabaseSavable(int id){
        this.id = id;
    }

    public DatabaseSavable(){}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    @Override
    public void setDirty(boolean bool) {
        this.dirty = bool;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DatabaseSavable){
            return id == ((DatabaseSavable) obj).id;
        }
        return false;
    }
}
