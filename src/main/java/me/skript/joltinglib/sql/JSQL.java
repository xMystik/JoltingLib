package me.skript.joltinglib.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JSQL {

    private Connection connection;
    private final String jdbcURL;

    /**
     * Initializes a new JSQL instance with a given SQLite database path
     *
     * @param path The file path to the SQLite database
     * @throws SQLException If a connection cannot be established
     */
    public JSQL(String path) throws SQLException {
        this.jdbcURL = "jdbc:sqlite:" + path;
        this.connection = DriverManager.getConnection(jdbcURL);
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    // Opens the connection on first use, Reopens it if it ever closes
    /**
     * Gets the database connection. Reopens the connection if it was closed.
     *
     * @return A valid SQL Connection.
     * @throws SQLException If the connection cannot be established.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connection = DriverManager.getConnection(jdbcURL);
        }
        return connection;
    }

    /**
     * Closes the database connection if it is open.
     * Recommended to call this in your plugin's {@code onDisable()} method.
     *
     * @throws SQLException If an error occurs while closing the connection.
     */
    public void closeConnection() throws SQLException {
        if(connection != null && !connection.isClosed()){
            try {
                connection.close();
            } catch (SQLException ignored) {
                System.out.println("[JSQL] Error closing connection");
            }
        }
    }

    /**
     * Executes an SQL update statement (e.g., INSERT, UPDATE, DELETE).
     *
     * @param sql    The SQL query to execute.
     * @param params The parameters to bind to the query (e.g. ? placeholders).
     * @return The number of rows affected.
     * @throws SQLException If an error occurs during execution.
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    /**
     * Executes an SQL SELECT query and maps each row to an object.
     *
     * @param sql     The SQL SELECT query to execute.
     * @param mapper  A RowMapper function to convert rows into objects.
     * @param params  The parameters to bind to the query.
     * @param <T>     The type of object to return in the result list.
     * @return A list of objects mapped from the result set.
     * @throws SQLException If an error occurs during execution.
     */
    public <T> List<T> executeQuery(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
                return results;
            }
        }
    }

    /**
     * Runs a block of SQL operations within a transaction.
     * Automatically commits if successful or rolls back on error.
     *
     * @param work A lambda accepting the active Connection to perform multiple operations.
     * @throws SQLException If the transaction fails.
     */
    public void runInTransaction(Consumer<Connection> work) throws SQLException {
        Connection connection = getConnection();
        boolean oldAuto = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);   // start transaction
            work.accept(connection);           // user supplied block
            connection.commit();               // all good: commit

        } catch (SQLException exception) {
            connection.rollback();             // something failed: undo
            throw exception;

        } finally {
            connection.setAutoCommit(oldAuto); //restore original mode
        }
    }

    /**
     * Runs basic schema migration for versioning the database.
     * Creates a default "users" table if the version is 0.
     * Can be customized or expanded to handle future schema upgrades.
     *
     * @throws SQLException If migration fails.
     */
    public void migrate() throws SQLException {
        runInTransaction(connection -> {
            try {
                ResultSet rs = connection.createStatement().executeQuery("PRAGMA user_version");
                int version = rs.next() ? rs.getInt(1) : 0;

                if (version < 1) {
                    connection.createStatement().execute("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT)");
                    connection.createStatement().execute("PRAGMA user_version = 1");
                }
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

//     HOW TO USE
//        public void onEnable() {
//            try {
//                JSQL sql = new JSQL(getDataFolder() + "/tags.db");
//                tagsDatabase = new TagsDatabase(sql);
//            } catch (SQLException e) {
//                getLogger().severe("Failed to initialize database");
//                e.printStackTrace();
//                getServer().getPluginManager().disablePlugin(this);
//            }
//        }

}
