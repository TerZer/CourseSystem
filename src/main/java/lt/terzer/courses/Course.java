package lt.terzer.courses;

import lt.terzer.sql.data.DatabaseSavable;
import lt.terzer.sql.data.SerializableList;

import java.util.Objects;

public class Course extends DatabaseSavable {

    private String name;
    private String description;
    private SerializableList fileIds;

    public Course(int id, String name, String description, SerializableList fileIds){
        super(id);
        this.name = name;
        this.description = description;
        this.fileIds = Objects.requireNonNullElseGet(fileIds, SerializableList::new);;
    }

    public Course(String name, String description){
        this(name, description, null);
    }

    public Course(String name, String description, SerializableList fileIds){
        this.name = name;
        this.description = description;
        this.fileIds = Objects.requireNonNullElseGet(fileIds, SerializableList::new);;
    }

    public SerializableList getFilesIds(){
        return fileIds;
    }

    public void addFileId(int id){
        fileIds.add(id);
        setDirty();
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
        setDirty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
