package id.co.jalin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SimpleConnectionPool {

    private List<Connection> availableConnections = new ArrayList<>();
    private List<Connection> usedConnections = new ArrayList<>();
    private static int INITIAL_POOL_SIZE;
    private String url;
    private String user;
    private String password;

    private static SimpleConnectionPool instance;

    private SimpleConnectionPool() throws SQLException {
        try {
            Properties properties = new Properties();
            try (InputStream input = SimpleConnectionPool.class.getClassLoader()
                    .getResourceAsStream("config/database.properties")) {
                if (input == null) {
                    throw new IOException("Database properties file not found");
                }
                properties.load(input);
            }
            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");
            INITIAL_POOL_SIZE = Integer.parseInt(properties.getProperty("db.pool.size", "10"));

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("MySQL JDBC driver not found.", e);
            }

            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableConnections.add(createConnection());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load database properties", e);
        }
    }

    public static SimpleConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            synchronized (SimpleConnectionPool.class) {
                if (instance == null) {
                    instance = new SimpleConnectionPool();
                }
            }
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public synchronized Connection getConnection() {
        if (availableConnections.isEmpty()) {
            throw new RuntimeException("Maximum pool size reached, no available connections!");
        }

        Connection connection = availableConnections.remove(availableConnections.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public synchronized void releaseConnection(Connection connection) {
        availableConnections.add(connection);
        usedConnections.remove(connection);
    }

    public int getAvailableConnectionsCount() {
        return availableConnections.size();
    }
}
