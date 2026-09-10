package jdbc.com.VehicleRentalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminOperation {

    // ================= ADMIN LOGIN =================
    public static void adminLogin(Scanner sc) {
        System.out.println("\n===== ADMIN LOGIN =====");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql = "SELECT admin_id, username FROM admin WHERE username = ? AND password = ?";

        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Admin login successful.");
                    adminMenu(sc);
                } else {
                    System.out.println("Invalid admin credentials.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    // ================= ADMIN PANEL =================
    private static void adminMenu(Scanner sc) {
        while (true) {
            System.out.println("\n===== ADMIN PANEL =====");
            System.out.println("1. Customer Management");
            System.out.println("2. Vehicle Management");
            System.out.println("3. Rental Management");
            System.out.println("4. Payment Management");
            System.out.println("5. Reports");
            System.out.println("6. Logout");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: customerManagement(sc); break;
                case 2: vehicleManagement(sc); break;
                case 3: rentalManagement(sc); break;
                case 4: paymentManagement(sc); break;
                case 5: reports(sc); break;
                case 6:
                    System.out.println("Admin logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ================= CUSTOMER MANAGEMENT =================
    private static void customerManagement(Scanner sc) {
        while (true) {
            System.out.println("\n===== CUSTOMER MANAGEMENT =====");
            System.out.println("1. View All Customers");
            System.out.println("2. Search Customer by ID");
            System.out.println("3. Search Customer by Phone");
            System.out.println("4. Update Customer Details");
            System.out.println("5. Delete Customer");
            System.out.println("6. Back");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: CustomerOperation.viewAllCustomers(); break;
                case 2: CustomerOperation.searchCustomerById(sc); break;
                case 3: CustomerOperation.searchCustomerByPhone(sc); break;
                case 4: CustomerOperation.updateCustomer(sc); break;
                case 5: CustomerOperation.deleteCustomer(sc); break;
                case 6: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ================= VEHICLE MANAGEMENT =================
    private static void vehicleManagement(Scanner sc) {
        while (true) {
            System.out.println("\n===== VEHICLE MANAGEMENT =====");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View All Vehicles");
            System.out.println("3. Search Vehicle");
            System.out.println("4. Update Vehicle");
            System.out.println("5. Delete Vehicle");
            System.out.println("6. View Available Vehicles");
            System.out.println("7. View Rented Vehicles");
            System.out.println("8. Send Vehicle to Maintenance");
            System.out.println("9. Back");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: VehicleOperation.addVehicle(sc); break;
                case 2: VehicleOperation.viewAllVehicles(); break;
                case 3: VehicleOperation.searchVehicle(sc); break;
                case 4: VehicleOperation.updateVehicle(sc); break;
                case 5: VehicleOperation.deleteVehicle(sc); break;
                case 6: VehicleOperation.viewAvailableVehicles(); break;
                case 7: VehicleOperation.viewRentedVehicles(); break;
                case 8: VehicleOperation.sendVehicleToMaintenance(sc); break;
                case 9: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ================= RENTAL MANAGEMENT =================
    private static void rentalManagement(Scanner sc) {
        while (true) {
            System.out.println("\n===== RENTAL MANAGEMENT =====");
            System.out.println("1. View All Rentals");
            System.out.println("2. View Active Rentals");
            System.out.println("3. View Completed Rentals");
            System.out.println("4. View Customer Rental History");
            System.out.println("5. Cancel Rental");
            System.out.println("6. Back");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: RentalOperation.viewAllRentals(); break;
                case 2: RentalOperation.viewActiveRentals(); break;
                case 3: RentalOperation.viewCompletedRentals(); break;
                case 4:
                    System.out.print("Enter Customer ID: ");
                    int cid = Integer.parseInt(sc.nextLine());
                    RentalOperation.viewCustomerRentalHistory(sc, cid);
                    break;
                case 5: RentalOperation.cancelRental(sc); break;
                case 6: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ================= PAYMENT MANAGEMENT =================
    private static void paymentManagement(Scanner sc) {
        while (true) {
            System.out.println("\n===== PAYMENT MANAGEMENT =====");
            System.out.println("1. View All Payments");
            System.out.println("2. View Paid Payments");
            System.out.println("3. View Pending Payments");
            System.out.println("4. View Payment by Rental ID");
            System.out.println("5. Back");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: PaymentOperation.viewAllPayments(); break;
                case 2: PaymentOperation.viewPaidPayments(); break;
                case 3: PaymentOperation.viewPendingPayments(); break;
                case 4: PaymentOperation.viewPaymentByRentalId(sc); break;
                case 5: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ================= REPORTS =================
    private static void reports(Scanner sc) {
        while (true) {
            System.out.println("\n===== REPORTS =====");
            System.out.println("1. Available Vehicles Report");
            System.out.println("2. Rented Vehicles Report");
            System.out.println("3. Customer Rental History");
            System.out.println("4. Vehicle Rental History");
            System.out.println("5. Total Revenue");
            System.out.println("6. Monthly Revenue");
            System.out.println("7. Pending Payments");
            System.out.println("8. Most Rented Vehicle");
            System.out.println("9. Back");

            int choice = Main.readInt("Enter choice: ");

            switch (choice) {
                case 1: ReportOperation.availableVehiclesReport(); break;
                case 2: ReportOperation.rentedVehiclesReport(); break;
                case 3: ReportOperation.customerRentalHistory(sc); break;
                case 4: ReportOperation.vehicleRentalHistory(sc); break;
                case 5: ReportOperation.totalRevenue(); break;
                case 6: ReportOperation.monthlyRevenue(); break;
                case 7: ReportOperation.pendingPayments(); break;
                case 8: ReportOperation.mostRentedVehicle(); break;
                case 9: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }
}