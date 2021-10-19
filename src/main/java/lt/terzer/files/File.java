package lt.terzer.files;

import lt.terzer.sql.data.DatabaseSavable;

public class File extends DatabaseSavable {

    private String name;

    public File(int id, String name) {
        super(id);
        this.name = name;
    }

    public File(String name) {
        this.name = name;
    }

    public boolean isFolder(){
        return false;
    }

}
