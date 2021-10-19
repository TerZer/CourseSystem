package lt.terzer.sql;

import lt.terzer.sql.data.Savable;
import lt.terzer.sql.filters.IdFilterable;
import oracle.ucp.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;

public abstract class AbstractDatabase<T extends Savable> implements Database<T>, IdFilterable<T> {

    private static final int RETRY_TIMES = 2;
    private DatabaseStatus status = DatabaseStatus.NOT_CONNECTED;
    private final String url, username, password;
    protected final String table;
    //private DataSaver saver = null;
    private boolean broken;

    public AbstractDatabase(String url, String database, String table, String username, String password){
        database = database.endsWith("/") ? StringUtils.removeEnd(database, "/") : database;
        if(url.startsWith("jdbc:mysql://")) {
            if(url.endsWith("/"))
                this.url = url+database+"?createDatabaseIfNotExist=true";
            else
                this.url = url+"/"+database+"?createDatabaseIfNotExist=true";
        }
        else {
            if (url.endsWith("/"))
                this.url = "jdbc:mysql://" + url + database+"?createDatabaseIfNotExist=true";
            else
                this.url = "jdbc:mysql://" + url + "/" + database+"?createDatabaseIfNotExist=true";
        }
        this.table = table;
        this.username = username;
        this.password = password;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find Driver!");
            status = DatabaseStatus.DRIVER_ERROR;
            broken = true;
            return;
        }
        close(connect());
        /*saver = new DataSaver(this);
        Timer timer = new Timer();
        timer.schedule(saver, 600, 600);*/
    }

    protected Connection connect(){
        for(int i = 0;i < RETRY_TIMES;i++) {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                if(connection == null)
                    throw new SQLException("Could not get connection");
                if(connection.isClosed() || !connection.isValid(200))
                    throw new SQLException("Connection is not valid");
                status = DatabaseStatus.CONNECTED;
                return connection;
            } catch (SQLException e) {
                System.out.println("Could not connect to SQL! Reason: " + e.getMessage());
            }
        }
        status = DatabaseStatus.NOT_CONNECTED;
        return null;
    }

    protected boolean tableExists(){
        Connection connection = connect();
        ResultSet resultSet = null;
        try {
            if(connection != null) {
                resultSet = connection.getMetaData().getTables(null, null, table, null);
                if (resultSet.next()) {
                    return resultSet.getString(3).equals(table);
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
        finally {
            try {
                if(resultSet != null)
                    resultSet.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
            close(connection);
        }
    }

    protected abstract boolean createTable();

    @Override
    public void shutdown() {
        //saver.cancel();
        /*save(new ArrayList<T>().stream()
                .filter(Objects::nonNull).filter(Savable::isDirty)
                .collect(Collectors.toList()));*/
        status = DatabaseStatus.NOT_CONNECTED;
    }

    protected boolean removeQuery(String where){
        Connection connection = connect();
        if(where == null || where.trim().equals(""))
            return false;
        if(connection == null)
            return false;
        if(!tableExists())
            return false;
        try {
            connection.createStatement().execute("DELETE FROM " + table + " WHERE " + where);
        } catch (SQLException e) {
            System.out.println("Could not delete from database " + e.getMessage());
            return false;
        }
        return true;
    }

    protected Pair<Connection, ResultSet> executeQuery(){
        return executeQuery(null);
    }

    protected Pair<Connection, ResultSet> executeQuery(String where){
        Connection connection = connect();
        if(connection == null) {
            return null;
        }
        if(!tableExists()) {
            return null;
        }
        ResultSet results = null;
        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM " + table;
            if(where != null && !where.trim().equals(""))
                sql += " WHERE " + where;

            results = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Could not retrieve data from the database " + e.getMessage());
        }
        return new Pair<>(connection, results);
    }

    protected String idsToString(List<Integer> ids){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i < ids.size();i++){
            stringBuilder.append(ids.get(i));
            if(i+1 != ids.size())
                stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    protected String namesToString(List<String> names){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i < names.size();i++){
            stringBuilder.append("'").append(names.get(i)).append("'");
            if(i+1 != names.size())
                stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean save(List<T> list) {
        if(list.isEmpty())
            return true;
        if(broken)
            return false;
        Connection connection = connect();
        if(connection == null) {
            return false;
        }
        if(!tableExists()) {
            if(!createTable())
                return false;
        }
        return saveData(connection, list);
    }

    protected abstract boolean saveData(Connection connection, List<T> list);

    protected void close(Connection connection){
        try {
            if(connection != null)
                connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public DatabaseStatus status() {
        return status;
    }
}
