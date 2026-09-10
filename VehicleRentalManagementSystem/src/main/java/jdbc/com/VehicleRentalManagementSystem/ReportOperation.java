package jdbc.com.VehicleRentalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ReportOperation {

    // 1. Available Vehicles Report
    public static void availableVehiclesReport() {

        String sql = " SELECT vehicle_id, vehicle_number, brand, model,vehicle_type, rent_per_day FROM Vehicle  WHERE status = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "AVAILABLE");

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== AVAILABLE VEHICLES ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("Vehicle ID     : " + rs.getInt("vehicle_id"));
                    System.out.println("Vehicle Number : " + rs.getString("vehicle_number"));
                    System.out.println("Brand          : " + rs.getString("brand"));
                    System.out.println("Model          : " + rs.getString("model"));
                    System.out.println("Vehicle Type   : " + rs.getString("vehicle_type"));
                    System.out.println("Rent Per Day   : " + rs.getDouble("rent_per_day"));
                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No available vehicles found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. Rented Vehicles Report
    public static void rentedVehiclesReport() {

        String sql = "  SELECT vehicle_id, vehicle_number, brand, model, vehicle_type, rent_per_day FROM Vehicle WHERE status = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "RENTED");

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== RENTED VEHICLES ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("Vehicle ID     : " + rs.getInt("vehicle_id"));
                    System.out.println("Vehicle Number : " + rs.getString("vehicle_number"));
                    System.out.println("Brand          : " + rs.getString("brand"));
                    System.out.println("Model          : " + rs.getString("model"));
                    System.out.println("Vehicle Type   : " + rs.getString("vehicle_type"));
                    System.out.println("Rent Per Day   : " + rs.getDouble("rent_per_day"));
                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No rented vehicles found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. Customer Rental History
    public static void customerRentalHistory(Scanner scanner) {

        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();

        String sql = " SELECT r.rental_id, r.vehicle_id, r.rental_date, r.return_date,  r.status, p.amount FROM Rental r   LEFT JOIN Payment p ON r.rental_id = p.rental_id WHERE r.customer_id = ? ORDER BY r.rental_date DESC";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== CUSTOMER RENTAL HISTORY ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("Rental ID   : " + rs.getInt("rental_id"));
                    System.out.println("Vehicle ID  : " + rs.getInt("vehicle_id"));
                    System.out.println("Rental Date : " + rs.getDate("rental_date"));
                    System.out.println("Return Date : " + rs.getDate("return_date"));
                    System.out.println("Status      : " + rs.getString("status"));
                    System.out.println("Amount      : " + rs.getDouble("amount"));
                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No rental history found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 4. Vehicle Rental History
    public static void vehicleRentalHistory(Scanner scanner) {

        System.out.print("Enter Vehicle ID: ");
        int vehicleId = scanner.nextInt();

        String sql = " SELECT r.rental_id, r.customer_id,r.rental_date, r.return_date,r.status, p.amount FROM Rental r  LEFT JOIN Payment p ON r.rental_id = p.rental_id  WHERE r.vehicle_id = ? ORDER BY r.rental_date DESC ";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== VEHICLE RENTAL HISTORY ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("Rental ID   : " + rs.getInt("rental_id"));
                    System.out.println("Customer ID : " + rs.getInt("customer_id"));
                    System.out.println("Rental Date : " + rs.getDate("rental_date"));
                    System.out.println("Return Date : " + rs.getDate("return_date"));
                    System.out.println("Status      : " + rs.getString("status"));
                    System.out.println("Amount      : " + rs.getDouble("amount"));
                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No rental history found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 5. Total Revenue
    public static void totalRevenue() {

        String sql = " SELECT COALESCE(SUM(amount), 0) AS total_revenue FROM Payment  WHERE status = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "PAID");

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("\n========== TOTAL REVENUE ==========");
                    System.out.println("Total Revenue : " + rs.getDouble("total_revenue"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 6. Monthly Revenue
    public static void monthlyRevenue() {

        String sql = " SELECT YEAR(payment_date) AS year,  MONTH(payment_date) AS month,  SUM(amount) AS monthly_revenue  FROM Payment   WHERE status = ? GROUP BY YEAR(payment_date), MONTH(payment_date)  ORDER BY year, month";

        try (Connection  con = ConnectionPool.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "PAID");

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== MONTHLY REVENUE ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("Year           : " + rs.getInt("year"));
                    System.out.println("Month          : " + rs.getInt("month"));
                    System.out.println("Monthly Revenue: " + rs.getDouble("monthly_revenue"));
                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No revenue found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 7. Pending Payments
    public static void pendingPayments() {

        String sql = "  SELECT payment_id, rental_id,   amount, payment_date, status FROM Payment  WHERE status = ?";

        try (Connection  con = ConnectionPool.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "PENDING");

            try (ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== PENDING PAYMENTS ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("Payment ID   : " + rs.getInt("payment_id"));
                    System.out.println("Rental ID    : " + rs.getInt("rental_id"));
                    System.out.println("Amount       : " + rs.getDouble("amount"));
                    System.out.println("Payment Date : " + rs.getDate("payment_date"));
                    System.out.println("Status       : " + rs.getString("status"));
                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No pending payments found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 8. Most Rented Vehicle
    public static void mostRentedVehicle() {

        String sql = " SELECT v.vehicle_id, v.vehicle_number,  v.brand, v.model,  COUNT(r.rental_id) AS rental_count FROM Vehicle v  JOIN Rental r   ON v.vehicle_id = r.vehicle_id  GROUP BY v.vehicle_id, v.vehicle_number,   v.brand, v.model ORDER BY rental_count DESC LIMIT 1";

        try (Connection  con = ConnectionPool.getConnection();

             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n========== MOST RENTED VEHICLE ==========");

            if (rs.next()) {

                System.out.println("Vehicle ID     : " + rs.getInt("vehicle_id"));
                System.out.println("Vehicle Number : " + rs.getString("vehicle_number"));
                System.out.println("Brand          : " + rs.getString("brand"));
                System.out.println("Model          : " + rs.getString("model"));
                System.out.println("Rental Count   : " + rs.getInt("rental_count"));

            } else {
                System.out.println("No rental records found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}