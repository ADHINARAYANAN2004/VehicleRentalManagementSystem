package jdbc.com.VehicleRentalManagementSystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class ReturnOperation {

    // ================= RETURN VEHICLE (TRANSACTION) =================
    public static void returnVehicle(Scanner sc, int customerId) {
        System.out.println("\n===== RETURN VEHICLE =====");
        System.out.print("Enter Rental ID: ");
        int rentalId = Integer.parseInt(sc.nextLine());

        String checkSql = "SELECT r.rental_id, r.vehicle_id, r.rental_date, r.return_date, v.rent_per_day "
                + "FROM Rental r JOIN Vehicle v ON r.vehicle_id = v.vehicle_id "
                + "WHERE r.rental_id = ? AND r.customer_id = ? AND r.status = 'ACTIVE'";

        Connection con = null;
        int vehicleId;
        LocalDate rentalDate, expectedReturn;
        double rentPerDay;

        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, rentalId);
                ps.setInt(2, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Active rental not found for this customer.");
                        return;
                    }
                    vehicleId = rs.getInt("vehicle_id");
                    rentalDate = rs.getDate("rental_date").toLocalDate();
                    expectedReturn = rs.getDate("return_date").toLocalDate();
                    rentPerDay = rs.getDouble("rent_per_day");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        } finally {
            ConnectionPool.releaseConnection(con);
        }

        System.out.print("Enter Actual Return Date (YYYY-MM-DD): ");
        String returnStr = sc.nextLine();

        LocalDate actualReturn;
        try {
            actualReturn = LocalDate.parse(returnStr);
        } catch (Exception e) {
            System.out.println("Invalid date format!");
            return;
        }

        if (actualReturn.isBefore(rentalDate)) {
            System.out.println("Return date cannot be before rental date!");
            return;
        }

        long days = ChronoUnit.DAYS.between(rentalDate, actualReturn);
        if (days == 0) days = 1;
        double totalAmount = days * rentPerDay;

        if (actualReturn.isAfter(expectedReturn)) {
            long lateDays = ChronoUnit.DAYS.between(expectedReturn, actualReturn);
            double lateFee = lateDays * rentPerDay * 0.10;
            totalAmount += lateFee;
            System.out.println("Late return! Late fee applied: " + lateFee);
        }

        System.out.println("\nRental Days   : " + days);
        System.out.println("Total Amount  : " + totalAmount);
        System.out.print("Confirm return? (yes/no): ");
        if (!sc.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("Return cancelled.");
            return;
        }

        // ==================== TRANSACTION ====================
        con = null;
        try {
            con = ConnectionPool.getConnection();
            con.setAutoCommit(false);

            String updateRental = "UPDATE Rental SET status = 'COMPLETED', return_date = ?, total_amount = ? "
                    + "WHERE rental_id = ? AND status = 'ACTIVE'";
            try (PreparedStatement ps = con.prepareStatement(updateRental)) {
                ps.setDate(1, Date.valueOf(actualReturn));
                ps.setDouble(2, totalAmount);
                ps.setInt(3, rentalId);
                if (ps.executeUpdate() == 0) throw new SQLException("Rental update failed.");
            }

            String updateVehicle = "UPDATE Vehicle SET status = 'AVAILABLE' WHERE vehicle_id = ? AND status = 'RENTED'";
            try (PreparedStatement ps = con.prepareStatement(updateVehicle)) {
                ps.setInt(1, vehicleId);
                if (ps.executeUpdate() == 0) throw new SQLException("Vehicle status update failed.");
            }

            String insertPayment = "INSERT INTO Payment (rental_id, amount, payment_date, status) "
                    + "VALUES (?, ?, ?, 'PENDING')";
            try (PreparedStatement ps = con.prepareStatement(insertPayment)) {
                ps.setInt(1, rentalId);
                ps.setDouble(2, totalAmount);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("\nVehicle returned successfully!");
            System.out.println("Rental Status: COMPLETED");
            System.out.println("Vehicle Status: AVAILABLE");
            System.out.println("Payment record created (PENDING). Please make payment.");

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); System.out.println("Transaction rolled back!"); }
                catch (SQLException ex) { System.out.println("Rollback error: " + ex.getMessage()); }
            }
            System.out.println("Return failed: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                ConnectionPool.releaseConnection(con);
            }
        }
    }

    // ================= VIEW BILL =================
    public static void viewBill(Scanner sc, int customerId) {
        System.out.print("Enter Rental ID: ");
        int rentalId = Integer.parseInt(sc.nextLine());

        String sql = "SELECT r.rental_id, r.rental_date, r.return_date, r.total_amount, r.status, "
                + "v.vehicle_number, v.brand, v.model, v.rent_per_day "
                + "FROM Rental r JOIN Vehicle v ON r.vehicle_id = v.vehicle_id "
                + "WHERE r.rental_id = ? AND r.customer_id = ?";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, rentalId);
                ps.setInt(2, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n========== BILL ==========");
                        System.out.println("Rental ID     : " + rs.getInt("rental_id"));
                        System.out.println("Vehicle       : " + rs.getString("brand") + " "
                                + rs.getString("model") + " (" + rs.getString("vehicle_number") + ")");
                        System.out.println("Rent Per Day  : " + rs.getDouble("rent_per_day"));
                        System.out.println("Rental Date   : " + rs.getDate("rental_date"));
                        System.out.println("Return Date   : " + rs.getDate("return_date"));
                        System.out.println("Total Amount  : " + rs.getDouble("total_amount"));
                        System.out.println("Status        : " + rs.getString("status"));
                        System.out.println("==========================");
                    } else {
                        System.out.println("Rental not found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }
}