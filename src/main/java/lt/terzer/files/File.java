package lt.terzer.files;

import lt.terzer.sql.data.DatabaseSavable;

import java.util.Date;

public class File extends DatabaseSavable {

    private final String name;
    private Date date;

    public File(int id, String name, Date date) {
        super(id);
        this.name = name;
        this.date = date;
    }

    public File(String name) {
        this.name = name;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public boolean isFolder(){
        return false;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate(){
        return date;
    }

    @Override
    public String toString(){
        return name;
    }

}
