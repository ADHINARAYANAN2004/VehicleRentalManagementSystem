package jdbc.com.VehicleRentalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerOperation {

    // ================= CUSTOMER MENU =================
    public static void customerMenu(Scanner sc, int customerId) {
        while (true) {
            System.out.println("\n===== CUSTOMER PANEL MENU =====");
            System.out.println("1. View Available Vehicles");
            System.out.println("2. Search Vehicle");
            System.out.println("3. Rent Vehicle");
            System.out.println("4. Return Vehicle");
            System.out.println("5. View My Rentals");
            System.out.println("6. View My Payments");
            System.out.println("7. View Bill");
            System.out.println("8. Make Payment");
            System.out.println("9. Logout");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: VehicleOperation.viewAvailableVehicles(); break;
                case 2: searchVehicleMenu(sc); break;
                case 3: RentalOperation.rentVehicle(sc, customerId); break;
                case 4: ReturnOperation.returnVehicle(sc, customerId); break;
                case 5: RentalOperation.viewCustomerRentalHistory(sc, customerId); break;
                case 6: PaymentOperation.viewCustomerPayments(customerId); break;
                case 7: ReturnOperation.viewBill(sc, customerId); break;
                case 8: PaymentOperation.makePayment(sc, customerId); break;
                case 9:
                    System.out.println("Customer logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void searchVehicleMenu(Scanner sc) {
        System.out.println("\n===== SEARCH VEHICLE =====");
        System.out.println("1. Search by Vehicle Type");
        System.out.println("2. Search by Brand");
        System.out.println("3. Search by Rent Range");

        int choice = Main.readInt("Enter choice: ");

        switch (choice) {
            case 1: searchByType(sc); break;
            case 2: VehicleOperation.searchVehicle(sc); break;
            case 3: searchByRentRange(sc); break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static void searchByType(Scanner sc) {
        System.out.print("Enter Vehicle Type: ");
        String type = sc.nextLine();
        String sql = "SELECT * FROM Vehicle WHERE vehicle_type = ? AND status = 'AVAILABLE'";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, type);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.println("ID: " + rs.getInt("vehicle_id")
                                + " | " + rs.getString("brand") + " " + rs.getString("model")
                                + " | " + rs.getString("vehicle_type")
                                + " | Rent/Day: " + rs.getDouble("rent_per_day"));
                    }
                    if (!found) System.out.println("No vehicles found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    private static void searchByRentRange(Scanner sc) {
        System.out.print("Enter Min Rent: ");
        double min = Double.parseDouble(sc.nextLine());
        System.out.print("Enter Max Rent: ");
        double max = Double.parseDouble(sc.nextLine());

        String sql = "SELECT * FROM Vehicle WHERE rent_per_day BETWEEN ? AND ? AND status = 'AVAILABLE'";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDouble(1, min);
                ps.setDouble(2, max);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.println("ID: " + rs.getInt("vehicle_id")
                                + " | " + rs.getString("brand") + " " + rs.getString("model")
                                + " | Rent/Day: " + rs.getDouble("rent_per_day"));
                    }
                    if (!found) System.out.println("No vehicles found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= CUSTOMER REGISTRATION =================
    public static void registerCustomer(Scanner sc) {
        System.out.println("\n===== CUSTOMER REGISTRATION =====");
        System.out.print("Id: ");
        int customerId = Integer.parseInt(sc.nextLine());
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("License Number: ");
        String licenseNumber = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql = "INSERT INTO Customer (customer_id, name, phone, email, license_number, password) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                ps.setString(2, name);
                ps.setString(3, phone);
                ps.setString(4, email);
                ps.setString(5, licenseNumber);
                ps.setString(6, password);

                int rows = ps.executeUpdate();
                if (rows > 0) System.out.println("Customer registered successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= CUSTOMER LOGIN =================
    public static int customerLogin(Scanner sc) {
        System.out.println("\n===== CUSTOMER LOGIN =====");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql = "SELECT customer_id, name FROM Customer WHERE email = ? AND password = ?";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int customerId = rs.getInt("customer_id");
                        System.out.println("Login successful. Welcome, " + rs.getString("name"));
                        return customerId;
                    } else {
                        System.out.println("Invalid customer credentials.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
        return -1;
    }

    // ================= VIEW ALL CUSTOMERS =================
    public static void viewAllCustomers() {
        String sql = "SELECT customer_id, name, phone, email, license_number FROM Customer";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                System.out.println("\n===== ALL CUSTOMERS =====");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: " + rs.getInt("customer_id")
                            + " | Name: " + rs.getString("name")
                            + " | Phone: " + rs.getString("phone")
                            + " | Email: " + rs.getString("email")
                            + " | License: " + rs.getString("license_number"));
                }
                if (!found) System.out.println("No customers found.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch customers: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= SEARCH BY ID =================
    public static void searchCustomerById(Scanner sc) {
        System.out.print("Enter Customer ID: ");
        int customerId = Integer.parseInt(sc.nextLine());

        String sql = "SELECT customer_id, name, phone, email, license_number FROM Customer WHERE customer_id = ?";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n===== CUSTOMER DETAILS =====");
                        System.out.println("ID: " + rs.getInt("customer_id"));
                        System.out.println("Name: " + rs.getString("name"));
                        System.out.println("Phone: " + rs.getString("phone"));
                        System.out.println("Email: " + rs.getString("email"));
                        System.out.println("License: " + rs.getString("license_number"));
                    } else {
                        System.out.println("Customer ID not found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= SEARCH BY PHONE =================
    public static void searchCustomerByPhone(Scanner sc) {
        System.out.print("Enter Phone: ");
        String phone = sc.nextLine();

        String sql = "SELECT customer_id, name, phone, email, license_number FROM Customer WHERE phone = ?";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, phone);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n===== CUSTOMER DETAILS =====");
                        System.out.println("ID: " + rs.getInt("customer_id"));
                        System.out.println("Name: " + rs.getString("name"));
                        System.out.println("Phone: " + rs.getString("phone"));
                        System.out.println("Email: " + rs.getString("email"));
                        System.out.println("License: " + rs.getString("license_number"));
                    } else {
                        System.out.println("Customer phone not found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= UPDATE =================
    public static void updateCustomer(Scanner sc) {
        System.out.print("Enter Customer ID to update: ");
        int customerId = Integer.parseInt(sc.nextLine());
        System.out.print("Enter New Name: ");
        String name = sc.nextLine();
        System.out.print("Enter New Phone: ");
        String phone = sc.nextLine();
        System.out.print("Enter New Email: ");
        String email = sc.nextLine();
        System.out.print("Enter New License Number: ");
        String licenseNumber = sc.nextLine();
        System.out.print("Enter New Password: ");
        String password = sc.nextLine();

        String sql = "UPDATE Customer SET name = ?, phone = ?, email = ?, license_number = ?, password = ? WHERE customer_id = ?";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setString(4, licenseNumber);
                ps.setString(5, password);
                ps.setInt(6, customerId);

                int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Customer updated successfully." : "Customer ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // ================= DELETE =================
    public static void deleteCustomer(Scanner sc) {
        System.out.print("Enter Customer ID to delete: ");
        int customerId = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM Customer WHERE customer_id = ?";
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Customer deleted successfully." : "Customer ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }
}
