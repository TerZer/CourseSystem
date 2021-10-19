package lt.terzer.databases;

import lt.terzer.sql.AbstractDatabase;
import lt.terzer.sql.data.DatabaseSavable;
import lt.terzer.sql.data.SerializableList;
import lt.terzer.sql.filters.UsernameFilterable;
import lt.terzer.user.Company;
import lt.terzer.user.User;
import oracle.ucp.util.Pair;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDatabase extends AbstractDatabase<User> implements UsernameFilterable<User> {


    public UserDatabase(String url, String database, String table, String username, String password) {
        super(url, database, table, username, password);
    }

    protected List<User> retrieveData(Pair<Connection, ResultSet> pair) {
        List<User> list = new ArrayList<>();
        if(pair.get2nd() != null) {
            try {
                while (pair.get2nd().next()) {
                    int id = pair.get2nd().getInt(1);
                    String username = pair.get2nd().getString(2);
                    String name = pair.get2nd().getString(3);
                    String password = pair.get2nd().getString(4);
                    String surname = pair.get2nd().getString(5);
                    String contactInformation = pair.get2nd().getString(6);
                    boolean company = pair.get2nd().getBoolean(7);
                    String companyName = pair.get2nd().getString(8);
                    boolean courseCreator = pair.get2nd().getBoolean(9);
                    boolean admin = pair.get2nd().getBoolean(10);
                    SerializableList editableCourses = SerializableList.deserialize(pair.get2nd().getString(11));
                    SerializableList accessibleCourses = SerializableList.deserialize(pair.get2nd().getString(12));
                    if(company){
                        list.add(new Company(id, companyName, username, name, surname, contactInformation, password
                                , courseCreator, admin, editableCourses, accessibleCourses));
                    }
                    else{
                        list.add(new User(id, username, name, surname, contactInformation, password
                                , courseCreator, admin, editableCourses, accessibleCourses));
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
    protected boolean createTable() {
        Connection connection = connect();
        Statement statement = null;
        try {
            if(connection != null) {
                statement = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS "+table+" " +
                        "(id INT AUTO_INCREMENT PRIMARY KEY," +
                        " username TEXT NOT NULL, " +
                        " name TEXT NOT NULL, " +
                        " password TEXT NOT NULL, " +
                        " surname TEXT NOT NULL, " +
                        " contactInformation TEXT NOT NULL, " +
                        " company BOOL NOT NULL," +
                        " companyName TEXT, " +
                        " courseCreator BOOL NOT NULL," +
                        " admin BOOL NOT NULL," +
                        " editableCourses TEXT," +
                        " accessibleCourses TEXT" +
                        " )";
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
    protected boolean saveData(Connection connection, List<User> users) {
        try {
            for (User user : users) {
                Statement stmt = connection.createStatement();
                if(user.getId() == -1){
                    stmt.executeUpdate(
                            "INSERT INTO "+table+" (username, name, password, surname, contactInformation, company, companyName, courseCreator, admin, editableCourses, accessibleCourses) "
                                    + "values ("
                                    + "'"+user.getUsername()+"',"
                                    + "'"+user.getName()+"',"
                                    + "'"+user.getPassword()+"',"
                                    + "'"+user.getSurname()+"',"
                                    + "'"+user.getContactInformation()+"',"
                                    + ""+user.isCompany()+","
                                    + "'"+(user.isCompany() ? ((Company)user).getCompanyName() : null)+"',"
                                    + ""+user.isCourseCreator()+","
                                    + ""+user.isAdmin()+","
                                    + "'"+user.getEditableCourses().serialize()+"',"
                                    + "'"+user.getAccessibleCourses().serialize()+"'"
                                    + ");",
                            Statement.RETURN_GENERATED_KEYS);
                }
                else{
                    stmt.executeUpdate(
                            "INSERT INTO "+table+" (id, username, name, password, surname, contactInformation, company, companyName, courseCreator, admin, editableCourses, accessibleCourses) "
                                    + "values ("
                                    + "'"+user.getId()+"',"
                                    + "'"+user.getUsername()+"',"
                                    + "'"+user.getName()+"',"
                                    + "'"+user.getPassword()+"',"
                                    + "'"+user.getSurname()+"',"
                                    + "'"+user.getContactInformation()+"',"
                                    + ""+user.isCompany()+","
                                    + "'"+(user.isCompany() ? ((Company)user).getCompanyName() : null)+"',"
                                    + ""+user.isCourseCreator()+","
                                    + ""+user.isAdmin()+","
                                    + "'"+user.getEditableCourses().serialize()+"',"
                                    + "'"+user.getAccessibleCourses().serialize()+"'"
                                    + ") ON DUPLICATE KEY UPDATE"
                                    + "  id = VALUES(id),"
                                    + "  username = VALUES(username),"
                                    + "  name = VALUES(name),"
                                    + "  password = VALUES(password),"
                                    + "  surname = VALUES(surname),"
                                    + "  contactInformation = VALUES(contactInformation),"
                                    + "  company = VALUES(company),"
                                    + "  companyName = VALUES(companyName),"
                                    + "  courseCreator = VALUES(courseCreator),"
                                    + "  admin = VALUES(admin),"
                                    + "  editableCourses = VALUES(editableCourses),"
                                    + "  accessibleCourses = VALUES(accessibleCourses);",
                            Statement.RETURN_GENERATED_KEYS);
                }
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
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

    @Override
    public boolean remove(List<User> users) {
        if(users.isEmpty())
            return true;
        return removeQuery("id in (" + idsToString(users.stream()
                .map(DatabaseSavable::getId).collect(Collectors.toList())) + ")");
    }

    @Override
    public List<User> getAll() {
        Pair<Connection, ResultSet> pair = executeQuery();
        return retrieveData(pair);
    }

    @Override
    public List<User> getByIds(List<Integer> ids) {
        if(ids.isEmpty())
            return new ArrayList<>();
        Pair<Connection, ResultSet> pair = executeQuery("id in (" + idsToString(ids) + ")");
        return retrieveData(pair);
    }

    @Override
    public List<User> getByUsernames(List<String> names) {
        if(names.isEmpty())
            return new ArrayList<>();
        Pair<Connection, ResultSet> pair = executeQuery("username in (" + namesToString(names) + ")");
        return retrieveData(pair);
    }
}
