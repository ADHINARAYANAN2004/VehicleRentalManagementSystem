package jdbc.com.VehicleRentalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class VehicleOperation {

    // 1. Add Vehicle
    public static void addVehicle(Scanner sc) {

        System.out.println("\n===== ADD VEHICLE =====");

        System.out.print("Vehicle Number: ");
        long vehicleNumber = Long.parseLong(sc.nextLine());

        System.out.print("Brand: ");
        String brand = sc.nextLine();

        System.out.print("Model: ");
        String model = sc.nextLine();

        System.out.print("Vehicle Type (Car, Bike, SUV, Scooter, etc.): ");
        String vehicleType = sc.nextLine();

        System.out.print("Rent Per Day: ");
        double rentPerDay = Double.parseDouble(sc.nextLine());

        System.out.print("Status (AVAILABLE / RENTED / MAINTENANCE): ");
        String status = sc.nextLine();

        String sql = "INSERT INTO Vehicle "
                + "(vehicle_number, brand, model, vehicle_type, rent_per_day, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, vehicleNumber);
            ps.setString(2, brand);
            ps.setString(3, model);
            ps.setString(4, vehicleType);
            ps.setDouble(5, rentPerDay);
            ps.setString(6, status);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Vehicle added successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Vehicle details error: " + e.getMessage());
        }
    }

    // 2. View All Vehicles
    public static void viewAllVehicles() {

        String sql = "SELECT * FROM Vehicle";

        try (Connection con = ConnectionPool.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL VEHICLES =====");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("vehicle_id")
                        + " | Number: " + rs.getLong("vehicle_number")
                        + " | Brand: " + rs.getString("brand")
                        + " | Model: " + rs.getString("model")
                        + " | Type: " + rs.getString("vehicle_type")
                        + " | Rent/Day: " + rs.getDouble("rent_per_day")
                        + " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No vehicles found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to fetch vehicles: " + e.getMessage());
        }
    }

    // 3. Search Vehicle by Brand
    public static void searchVehicle(Scanner sc) {

        System.out.print("Enter Brand: ");
        String brand = sc.nextLine();

        String sql = "SELECT vehicle_id, vehicle_number, brand, model, "
                + "vehicle_type, rent_per_day, status "
                + "FROM Vehicle WHERE brand = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, brand);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\n===== SEARCH VEHICLE =====");

                while (rs.next()) {
                    found = true;

                    System.out.println(
                            "ID: " + rs.getInt("vehicle_id")
                            + " | Number: " + rs.getLong("vehicle_number")
                            + " | Brand: " + rs.getString("brand")
                            + " | Model: " + rs.getString("model")
                            + " | Type: " + rs.getString("vehicle_type")
                            + " | Rent/Day: " + rs.getDouble("rent_per_day")
                            + " | Status: " + rs.getString("status")
                    );
                }

                if (!found) {
                    System.out.println("No vehicles found for brand: " + brand);
                }
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    // 4. Update Vehicle
    public static void updateVehicle(Scanner sc) {

        System.out.print("Enter Vehicle ID to update: ");
        int vehicleId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter New Vehicle Number: ");
        long vehicleNumber = Long.parseLong(sc.nextLine());

        System.out.print("Enter New Brand: ");
        String brand = sc.nextLine();

        System.out.print("Enter New Model: ");
        String model = sc.nextLine();

        System.out.print("Enter New Vehicle Type: ");
        String vehicleType = sc.nextLine();

        System.out.print("Enter New Rent Per Day: ");
        double rentPerDay = Double.parseDouble(sc.nextLine());

        System.out.print("Enter New Status: ");
        String status = sc.nextLine();

        String sql = "UPDATE Vehicle SET vehicle_number = ?, brand = ?, "
                + "model = ?, vehicle_type = ?, rent_per_day = ?, status = ? "
                + "WHERE vehicle_id = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, vehicleNumber);
            ps.setString(2, brand);
            ps.setString(3, model);
            ps.setString(4, vehicleType);
            ps.setDouble(5, rentPerDay);
            ps.setString(6, status);
            ps.setInt(7, vehicleId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Vehicle updated successfully.");
            } else {
                System.out.println("Vehicle ID not found.");
            }

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // 5. Delete Vehicle
    public static void deleteVehicle(Scanner sc) {

        System.out.print("Enter Vehicle ID to delete: ");
        int vehicleId = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM Vehicle WHERE vehicle_id = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Vehicle deleted successfully.");
            } else {
                System.out.println("Vehicle ID not found.");
            }

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // 6. View Available Vehicles
    public static void viewAvailableVehicles() {

        String sql = "SELECT vehicle_id, vehicle_number, brand, model, "
                + "vehicle_type, rent_per_day, status "
                + "FROM Vehicle WHERE status = 'AVAILABLE'";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean found = false;

            System.out.println("\n===== AVAILABLE VEHICLES =====");

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("vehicle_id")
                        + " | Number: " + rs.getLong("vehicle_number")
                        + " | Brand: " + rs.getString("brand")
                        + " | Model: " + rs.getString("model")
                        + " | Type: " + rs.getString("vehicle_type")
                        + " | Rent/Day: " + rs.getDouble("rent_per_day")
                        + " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No available vehicles found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to fetch vehicles: " + e.getMessage());
        }
    }

    // 7. View Rented Vehicles
    public static void viewRentedVehicles() {

        String sql = "SELECT vehicle_id, vehicle_number, brand, model, "
                + "vehicle_type, rent_per_day, status "
                + "FROM Vehicle WHERE status = 'RENTED'";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean found = false;

            System.out.println("\n===== RENTED VEHICLES =====");

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("vehicle_id")
                        + " | Number: " + rs.getLong("vehicle_number")
                        + " | Brand: " + rs.getString("brand")
                        + " | Model: " + rs.getString("model")
                        + " | Type: " + rs.getString("vehicle_type")
                        + " | Rent/Day: " + rs.getDouble("rent_per_day")
                        + " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No rented vehicles found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to fetch rented vehicles: " + e.getMessage());
        }
    }

    // 8. Send Vehicle to Maintenance
    public static void sendVehicleToMaintenance(Scanner sc) {

        System.out.print("Enter Vehicle ID: ");
        int vehicleId = Integer.parseInt(sc.nextLine());

        String checkSql = "SELECT status FROM Vehicle WHERE vehicle_id = ?";

        String updateSql = "UPDATE Vehicle SET status = 'MAINTENANCE' "
                + "WHERE vehicle_id = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {

            checkPs.setInt(1, vehicleId);

            try (ResultSet rs = checkPs.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Vehicle ID not found.");
                    return;
                }

                String currentStatus = rs.getString("status");

                if ("RENTED".equalsIgnoreCase(currentStatus)) {
                    System.out.println("Cannot send a rented vehicle to maintenance.");
                    return;
                }
            }

            try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {

                updatePs.setInt(1, vehicleId);

                int rows = updatePs.executeUpdate();

                if (rows > 0) {
                    System.out.println("Vehicle sent to maintenance successfully.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Maintenance update failed: " + e.getMessage());
        }
    }
}
