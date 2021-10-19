package lt.terzer.sql;

import lt.terzer.sql.data.Savable;

import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class DataSaver extends TimerTask {

    private final Database<? extends Savable> database;

    public DataSaver(Database<? extends Savable> database){
        this.database = database;
    }

    @Override
    public void run() {
        List list = database.getAll().stream()
                .filter(Objects::nonNull).filter(Savable::isDirty)
                .collect(Collectors.toList());
        boolean saved = database.save(list);
        if(!saved) {
            System.out.println("Could not save to database error occurred!");
        }
    }
}
