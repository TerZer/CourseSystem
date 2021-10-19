package lt.terzer.files;

import lt.terzer.sql.data.SerializableList;

import java.util.ArrayList;
import java.util.List;

public class Folder extends File {

    SerializableList files;

    public Folder(int id, String name, SerializableList files) {
        super(id, name);
        this.files = files;
    }

    public Folder(String name) {
        super(name);
    }

    @Override
    public boolean isFolder(){
        return true;
    }

}
