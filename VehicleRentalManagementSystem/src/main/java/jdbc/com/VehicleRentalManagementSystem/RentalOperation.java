package jdbc.com.VehicleRentalManagementSystem;


import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class RentalOperation {

    // ================= RENT VEHICLE (TRANSACTION) =================
    public static void rentVehicle(Scanner sc, int customerId) {
        System.out.println("\n===== RENT A VEHICLE =====");
        VehicleOperation.viewAvailableVehicles();

        System.out.print("\nEnter Vehicle ID to rent: ");
        int vehicleId = Integer.parseInt(sc.nextLine());

        double rentPerDay;
        String checkSql = "SELECT rent_per_day FROM Vehicle WHERE vehicle_id = ? AND status = 'AVAILABLE'";
        Connection con = null;

        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, vehicleId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Vehicle not available!");
                        return;
                    }
                    rentPerDay = rs.getDouble("rent_per_day");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        } finally {
            ConnectionPool.releaseConnection(con);
        }

        System.out.print("Enter Rental Date (YYYY-MM-DD): ");
        String startStr = sc.nextLine();
        System.out.print("Enter Expected Return Date (YYYY-MM-DD): ");
        String endStr = sc.nextLine();

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startStr);
            endDate = LocalDate.parse(endStr);
        } catch (Exception e) {
            System.out.println("Invalid date format!");
            return;
        }

        if (endDate.isBefore(startDate)) {
            System.out.println("Return date cannot be before rental date!");
            return;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) days = 1;
        double totalAmount = days * rentPerDay;

        System.out.println("\nRental Days  : " + days);
        System.out.println("Total Amount : " + totalAmount);
        System.out.print("Confirm rental? (yes/no): ");
        if (!sc.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("Rental cancelled.");
            return;
        }

        // ==================== TRANSACTION ====================
        con = null;
        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false);

            String insertRental = "INSERT INTO Rental "
                    + "(customer_id, vehicle_id, rental_date, return_date, total_amount, status) "
                    + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
            try (PreparedStatement ps = con.prepareStatement(insertRental, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setInt(2, vehicleId);
                ps.setDate(3, Date.valueOf(startDate));
                ps.setDate(4, Date.valueOf(endDate));
                ps.setDouble(5, totalAmount);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) System.out.println("Rental ID: " + rs.getInt(1));
                }
            }

            String updateVehicle = "UPDATE Vehicle SET status = 'RENTED' WHERE vehicle_id = ? AND status = 'AVAILABLE'";
            try (PreparedStatement ps = con.prepareStatement(updateVehicle)) {
                ps.setInt(1, vehicleId);
                if (ps.executeUpdate() == 0) throw new SQLException("Vehicle status update failed.");
            }

            con.commit();
            System.out.println("Vehicle rented successfully!");

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); System.out.println("Transaction rolled back!"); }
                catch (SQLException ex) { System.out.println("Rollback error: " + ex.getMessage()); }
            }
            System.out.println("Rental failed: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                ConnectionPool.releaseConnection(con);
            }
        }
    }

    // ================= VIEW ALL RENTALS =================
    public static void viewAllRentals() {
        String sql = "SELECT rental_id, customer_id, vehicle_id, rental_date, return_date, status FROM Rental";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                System.out.println("\n========== ALL RENTALS ==========");
                boolean found = false;
                while (rs.next()) { found = true; displayRental(rs); }
                if (!found) System.out.println("No rentals found.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch rentals: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= VIEW ACTIVE / COMPLETED =================
    public static void viewActiveRentals() { viewRentalsByStatus("ACTIVE", "ACTIVE RENTALS"); }
    public static void viewCompletedRentals() { viewRentalsByStatus("COMPLETED", "COMPLETED RENTALS"); }

    private static void viewRentalsByStatus(String status, String title) {
        String sql = "SELECT rental_id, customer_id, vehicle_id, rental_date, return_date, status FROM Rental WHERE status = ?";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n========== " + title + " ==========");
                    boolean found = false;
                    while (rs.next()) { found = true; displayRental(rs); }
                    if (!found) System.out.println("No " + status.toLowerCase() + " rentals found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch rentals: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= CUSTOMER RENTAL HISTORY =================
    public static void viewCustomerRentalHistory(Scanner scanner, int customerId) {
        String sql = "SELECT rental_id, customer_id, vehicle_id, rental_date, return_date, status "
                + "FROM Rental WHERE customer_id = ? ORDER BY rental_date DESC";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n========== MY RENTAL HISTORY ==========");
                    boolean found = false;
                    while (rs.next()) { found = true; displayRental(rs); }
                    if (!found) System.out.println("No rental history found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch rental history: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= CANCEL RENTAL (TRANSACTION) =================
    public static void cancelRental(Scanner scanner) {
        System.out.print("Enter Rental ID: ");
        int rentalId = Integer.parseInt(scanner.nextLine());

        String checkSql = "SELECT vehicle_id FROM Rental WHERE rental_id = ? AND status = 'ACTIVE'";
        String rentalSql = "UPDATE Rental SET status = 'CANCELLED' WHERE rental_id = ? AND status = 'ACTIVE'";
        String vehicleSql = "UPDATE Vehicle SET status = 'AVAILABLE' WHERE vehicle_id = ? AND status = 'RENTED'";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement checkPs = con.prepareStatement(checkSql);
                 PreparedStatement rentalPs = con.prepareStatement(rentalSql);
                 PreparedStatement vehiclePs = con.prepareStatement(vehicleSql)) {

                int vehicleId;
                checkPs.setInt(1, rentalId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Rental not found or already completed/cancelled.");
                        con.rollback();
                        return;
                    }
                    vehicleId = rs.getInt("vehicle_id");
                }

                rentalPs.setInt(1, rentalId);
                if (rentalPs.executeUpdate() == 0) throw new SQLException("Rental cancellation failed.");

                vehiclePs.setInt(1, vehicleId);
                if (vehiclePs.executeUpdate() == 0) throw new SQLException("Vehicle status update failed.");

                con.commit();
                System.out.println("Rental cancelled successfully.");
                System.out.println("Vehicle returned to AVAILABLE.");

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Cancellation failed. Changes rolled back.");
                System.out.println("Error: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= DISPLAY HELPER =================
    private static void displayRental(ResultSet rs) throws SQLException {
        System.out.println("--------------------------------");
        System.out.println("Rental ID   : " + rs.getInt("rental_id"));
        System.out.println("Customer ID : " + rs.getInt("customer_id"));
        System.out.println("Vehicle ID  : " + rs.getInt("vehicle_id"));
        System.out.println("Rental Date : " + rs.getDate("rental_date"));
        System.out.println("Return Date : " + rs.getDate("return_date"));
        System.out.println("Status      : " + rs.getString("status"));
        System.out.println("--------------------------------");
    }
}