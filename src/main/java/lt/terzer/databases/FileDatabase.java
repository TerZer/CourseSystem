package lt.terzer.databases;

import lt.terzer.courses.Course;
import lt.terzer.files.File;
import lt.terzer.files.Folder;
import lt.terzer.sql.AbstractDatabase;
import lt.terzer.sql.data.DatabaseSavable;
import lt.terzer.sql.data.SerializableList;
import oracle.ucp.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileDatabase extends AbstractDatabase<File> {

    public FileDatabase(String url, String database, String table, String username) {
        super(url, database, table, username);
    }

    public FileDatabase(String url, String database, String table, String username, String password) {
        super(url, database, table, username, password);
    }

    @Override
    public List<File> getByIds(List<Integer> ids) {
        if(ids.isEmpty())
            return new ArrayList<>();
        Pair<Connection, ResultSet> pair = executeQuery("id in (" + idsToString(ids) + ")");
        return retrieveData(pair);
    }

    private List<File> retrieveData(Pair<Connection, ResultSet> pair){
        List<File> list = new ArrayList<>();
        if(pair.get2nd() != null) {
            try {
                while (pair.get2nd().next()) {
                    if(pair.get2nd().getBoolean(3)) {
                        list.add(new Folder(pair.get2nd().getInt(1), pair.get2nd().getString(2), SerializableList.deserialize(pair.get2nd().getString(4))));
                    }
                    else{
                        list.add(new File(pair.get2nd().getInt(1), pair.get2nd().getString(2)));
                    }
                }
            } catch (SQLException e) {
                System.out.println("Could not retrieve data from the database " + e.getMessage());
            }
            finally {
                close(pair.get1st());
            }
        }
        return list;
    }

    @Override
    public boolean remove(List<File> courses) {
        if(courses.isEmpty())
            return true;
        return removeQuery("id in (" + idsToString(courses.stream()
                .map(DatabaseSavable::getId).collect(Collectors.toList())) + ")");
    }

    @Override
    public List<File> getAll() {
        Pair<Connection, ResultSet> pair = executeQuery();
        return retrieveData(pair);
    }

    @Override
    protected boolean createTable() {
        Connection connection = connect();
        Statement statement = null;
        try {
            if(connection != null) {
                statement = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS "+table+" " +
                        "(id INT AUTO_INCREMENT PRIMARY KEY," +
                        " name TEXT NOT NULL, " +
                        " folder BOOL NOT NULL, " +
                        " files TEXT)";
                statement.executeUpdate(sql);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(statement != null){
                    statement.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
            close(connection);
        }
        return false;
    }

    @Override
    protected boolean saveData(Connection connection, List<File> files) {
        try {
            for (File file : files) {
                Statement stmt = connection.createStatement();
                if(file.getId() == -1){
                    stmt.executeUpdate(
                            "INSERT INTO "+table+" (name, folder, files) "
                                    + "values ("
                                    + "'"+file.getName()+"',"
                                    + ""+file.isFolder()+","
                                    + "'"+(file.isFolder() ? ((Folder) file).getFiles().serialize() : null)+"'"
                                    + ");",
                            Statement.RETURN_GENERATED_KEYS);
                }
                else{
                    stmt.executeUpdate(
                            "INSERT INTO "+table+" (id, name, folder, files) "
                                    + "values ("
                                    + "'"+file.getId()+"',"
                                    + "'"+file.getName()+"',"
                                    + ""+file.isFolder()+","
                                    + "'"+(file.isFolder() ? ((Folder) file).getFiles().serialize() : null)+"'"
                                    + ") ON DUPLICATE KEY UPDATE"
                                    + "  id = VALUES(id),"
                                    + "  name = VALUES(name),"
                                    + "  folder = VALUES(folder),"
                                    + "  files = VALUES(files);",
                            Statement.RETURN_GENERATED_KEYS);
                }
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    file.setId(id);
                } else {
                    System.out.println("Could not receive ID!");
                }
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
