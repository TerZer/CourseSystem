package lt.terzer.files;

import lt.terzer.sql.data.SerializableList;

import java.util.Date;

public class Folder extends File {

    private SerializableList files = new SerializableList();

    public Folder(int id, String name, SerializableList files, Date date) {
        super(id, name, date);
        this.files = files;
    }

    public Folder(String name) {
        super(name);
    }

    public void setFiles(SerializableList files) {
        this.files = files;
    }

    public void addFile(File file){
        addFile(file.getId());
    }

    public void addFile(int id){
        files.add(id);
    }

    public SerializableList getFiles(){
        return files;
    }

    @Override
    public boolean isFolder(){
        return true;
    }

    public void removeFile(Integer id) {
        files.remove(id);
    }
}
