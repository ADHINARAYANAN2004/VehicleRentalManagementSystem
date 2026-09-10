package jdbc.com.VehicleRentalManagementSystem;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {

    private static final int SIZE = 5;
    private static final BlockingQueue<Connection> pool = new ArrayBlockingQueue<>(SIZE);

    static {
        try {
            for (int i = 0; i < SIZE; i++) {
                Connection con = DBConnection.getConnection();
                if (con != null) pool.put(con);
            }
            System.out.println("Connection Pool Created. Available: " + pool.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while getting connection", e);
        }
    }

    public static void releaseConnection(Connection con) {
        if (con != null) {
            try {
                if (!con.isClosed()) {
                    con.setAutoCommit(true); // reset for safety
                    pool.put(con);
                }
            } catch (SQLException | InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    public static int availableConnections() {
        return pool.size();
    }

    public static void shutdown() {
        for (Connection con : pool) {
            try { con.close(); } catch (SQLException ignored) {}
        }
        pool.clear();
        System.out.println("Connection Pool shut down.");
    }
}
