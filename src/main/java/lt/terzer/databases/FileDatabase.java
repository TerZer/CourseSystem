package lt.terzer.databases;

import lt.terzer.files.File;
import lt.terzer.sql.AbstractDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//TODO create file database
/*public class FileDatabase extends AbstractDatabase<File> {


    public FileDatabase(java.io.File dataFolder, String url, String database, String table, String username, String password) {
        super(dataFolder, url, database, table, username, password);
    }

    @Override
    protected List<File> retrieveData() {
        List<File> list = new ArrayList<>();
        Connection connection = connect();
        if(connection == null) {
            return list;
        }
        if(!tableExists()) {
            return list;
        }
        Statement statement = null;
        ResultSet results = null;
        try {
            statement = connection.createStatement();
            results = statement.executeQuery("SELECT * FROM " + table);
            while (results.next()) {
                list.add(new SPPlayer(results.getString(2), UUID.fromString(results.getString(1))
                        , results.getLong(3), results.getLong(4), results.getLong(5)
                        , main.getRankHandler().getRankByName(results.getString(6))));
            }
            for(File file : list){
                int i = this.players.indexOf(player);
                if(i >= 0){
                    SPPlayer saved = this.players.get(i);
                    if(saved.isRuntimeCreated()){
                        saved.setKills(player.getKills()+saved.getKills());
                        saved.setDeaths(player.getDeaths()+saved.getDeaths());
                        saved.setTimePlayed(player.getTimePlayed()+saved.getTimePlayed());
                        saved.setRank(main.getRankHandler().compare(player.getRank(), saved.getRank()));
                        saved.setRuntimeCreated(false);
                    }
                }
                else{
                    this.players.add(player);
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not retrieve data from the database " + e.getMessage());
        }
        finally {
            try {
                if(statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(results != null)
                    results.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
            close(connection);
        }
        return list;
    }

    @Override
    protected boolean saveData() {
        final int batchSize = 1000; //Batch size is important.
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO "+table+" (uuid, name, kills, deaths, time_played, rank)" +
                    " VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE" +
                    "  kills = VALUES(kills)," +
                    "  deaths = VALUES(deaths)," +
                    "  time_played = VALUES(time_played)," +
                    "  rank = VALUES(rank);";
            ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            int insertCount=0;
            for (SPPlayer player : players) {
                ps.setString(1, player.getUUID().toString());
                ps.setString(2, player.getName());
                ps.setLong(3, player.getKills());
                ps.setLong(4, player.getDeaths());
                ps.setLong(5, player.getTimePlayed());
                ps.setString(6, player.getRank().getName());
                ps.addBatch();
                if (++insertCount % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if(ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(connection);
        }
        players.forEach(p -> {
            p.setRuntimeCreated(false);
            p.setDirty(false);
        });
        return true;
    }

    @Override
    protected boolean createTable() {
        Connection connection = connect();
        Statement statement = null;
        try {
            if(connection != null) {
                statement = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS "+table+" " +
                        "(uuid VARCHAR(36) NOT NULL UNIQUE, " +
                        " name VARCHAR(255) NOT NULL UNIQUE, " +
                        " kills BIGINT, " +
                        " deaths BIGINT, " +
                        " time_played BIGINT," +
                        " rank TEXT NOT NULL " +
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
}*/
