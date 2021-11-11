package lt.terzer.databases;

import lt.terzer.courses.Course;
import lt.terzer.sql.AbstractDatabase;
import lt.terzer.sql.data.DatabaseSavable;
import lt.terzer.sql.data.SerializableList;
import lt.terzer.user.Company;
import lt.terzer.user.User;
import oracle.ucp.util.Pair;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDatabase extends AbstractDatabase<Course> {

    public CourseDatabase(String url, String database, String table, String username) {
        super(url, database, table, username);
    }

    public CourseDatabase(String url, String database, String table, String username, String password) {
        super(url, database, table, username, password);
    }

    @Override
    public List<Course> getByIds(List<Integer> ids) {
        if(ids.isEmpty())
            return new ArrayList<>();
        Pair<Connection, ResultSet> pair = executeQuery("id in (" + idsToString(ids) + ")");
        return retrieveData(pair);
    }

    private List<Course> retrieveData(Pair<Connection, ResultSet> pair){
        List<Course> list = new ArrayList<>();
        if(pair.get2nd() != null) {
            try {
                while (pair.get2nd().next()) {
                    list.add(new Course(pair.get2nd().getInt(1), pair.get2nd().getString(2),
                            pair.get2nd().getString(3), SerializableList.deserialize(pair.get2nd().getString(4))));
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
    public boolean remove(List<Course> courses) {
        if(courses.isEmpty())
            return true;
        return removeQuery("id in (" + idsToString(courses.stream()
                .map(DatabaseSavable::getId).collect(Collectors.toList())) + ")");
    }

    @Override
    public List<Course> getAll() {
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
                        " description TEXT, " +
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
    protected boolean saveData(Connection connection, List<Course> courses) {
        try {
            for (Course course : courses) {
                Statement stmt = connection.createStatement();
                if(course.getId() == -1){
                    stmt.executeUpdate(
                            "INSERT INTO "+table+" (name, description, files) "
                                    + "values ("
                                    + "'"+course.getName()+"',"
                                    + "'"+course.getDescription()+"',"
                                    + "'"+course.getFilesIds().serialize()+"'"
                                    + ");",
                            Statement.RETURN_GENERATED_KEYS);
                }
                else{
                    stmt.executeUpdate(
                            "INSERT INTO "+table+" (id, name, description, files) "
                                    + "values ("
                                    + "'"+course.getId()+"',"
                                    + "'"+course.getName()+"',"
                                    + "'"+course.getDescription()+"',"
                                    + "'"+course.getFilesIds().serialize()+"'"
                                    + ") ON DUPLICATE KEY UPDATE"
                                    + "  id = VALUES(id),"
                                    + "  name = VALUES(name),"
                                    + "  description = VALUES(description),"
                                    + "  files = VALUES(files);",
                            Statement.RETURN_GENERATED_KEYS);
                }
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    course.setId(id);
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
