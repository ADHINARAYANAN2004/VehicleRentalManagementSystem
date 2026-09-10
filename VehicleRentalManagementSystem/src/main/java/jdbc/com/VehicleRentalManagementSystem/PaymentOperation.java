package jdbc.com.VehicleRentalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class PaymentOperation {

    // 1. View All Payments
    public static void viewAllPayments() {

        String sql = "SELECT payment_id, rental_id, amount, "
                   + "payment_date, status FROM Payment";

        Connection con = null;

        try {
            con = ConnectionPool.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                System.out.println("\n========== ALL PAYMENTS ==========");

                boolean found = false;

                while (rs.next()) {
                    found = true;
                    displayPayment(rs);
                }

                if (!found) {
                    System.out.println("No payments found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }


    // 2. View Paid Payments
    public static void viewPaidPayments() {

        String sql = "SELECT payment_id, rental_id, amount, "
                   + "payment_date, status FROM Payment WHERE status = ?";

        Connection con = null;

        try {
            con = ConnectionPool.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, "PAID");

                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n========== PAID PAYMENTS ==========");

                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        displayPayment(rs);
                    }

                    if (!found) {
                        System.out.println("No paid payments found.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }


    // 3. View Pending Payments
    public static void viewPendingPayments() {

        String sql = "SELECT payment_id, rental_id, amount, "
                   + "payment_date, status FROM Payment WHERE status = ?";

        Connection con = null;

        try {
            con = ConnectionPool.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, "PENDING");

                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n========== PENDING PAYMENTS ==========");

                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        displayPayment(rs);
                    }

                    if (!found) {
                        System.out.println("No pending payments found.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }


    // 4. View Payment by Rental ID
    public static void viewPaymentByRentalId(Scanner scanner) {

    	System.out.print("Enter Rental ID: ");
        int rentalId = Integer.parseInt(scanner.nextLine());

        String sql = "SELECT payment_id, rental_id, amount, "
                   + "payment_date, status FROM Payment WHERE rental_id = ?";

        Connection con = null;

        try {
            con = ConnectionPool.getConnection();

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, rentalId);

                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n========== PAYMENT DETAILS ==========");

                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        displayPayment(rs);
                    }

                    if (!found) {
                        System.out.println("No payment found for this rental.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }
 // View payments for a specific customer
    public static void viewCustomerPayments(int customerId) {

        String sql = "SELECT p.payment_id, p.rental_id, p.amount, "
                + "p.payment_date, p.status "
                + "FROM Payment p "
                + "JOIN Rental r ON p.rental_id = r.rental_id "
                + "WHERE r.customer_id = ? "
                + "ORDER BY p.payment_date DESC";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, customerId);

                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n========== MY PAYMENTS ==========");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        displayPayment(rs);
                    }
                    if (!found) System.out.println("No payments found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // Make payment
    public static void makePayment(Scanner sc, int customerId) {

        System.out.println("\n===== MAKE PAYMENT =====");
        viewCustomerPayments(customerId);

        System.out.print("\nEnter Payment ID to pay: ");
        int paymentId = Integer.parseInt(sc.nextLine());

        System.out.println("Payment Methods: 1. CASH  2. UPI  3. CARD");
        System.out.print("Choose method: ");
        int methodChoice = Integer.parseInt(sc.nextLine());

        String method;
        switch (methodChoice) {
            case 1: method = "CASH"; break;
            case 2: method = "UPI"; break;
            case 3: method = "CARD"; break;
            default:
                System.out.println("Invalid method!");
                return;
        }

        String sql = "UPDATE Payment SET status = 'PAID', payment_method = ? "
                + "WHERE payment_id = ? AND status = 'PENDING' "
                + "AND rental_id IN (SELECT rental_id FROM Rental WHERE customer_id = ?)";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, method);
                ps.setInt(2, paymentId);
                ps.setInt(3, customerId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Payment successful via " + method + "!");
                } else {
                    System.out.println("Payment not found or already paid.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Payment failed: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }

    // View payment status for a rental
    public static void viewPaymentStatus(Scanner sc, int customerId) {
        System.out.print("Enter Rental ID: ");
        int rentalId = Integer.parseInt(sc.nextLine());

        String sql = "SELECT p.* FROM Payment p "
                + "JOIN Rental r ON p.rental_id = r.rental_id "
                + "WHERE p.rental_id = ? AND r.customer_id = ?";

        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, rentalId);
                ps.setInt(2, customerId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        displayPayment(rs);
                    } else {
                        System.out.println("No payment found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(con);
        }
    }


    // Common method to display payment details
    private static void displayPayment(ResultSet rs) throws SQLException {

        System.out.println("Payment ID   : " + rs.getInt("payment_id"));
        System.out.println("Rental ID    : " + rs.getInt("rental_id"));
        System.out.println("Amount       : " + rs.getDouble("amount"));
        System.out.println("Payment Date : " + rs.getDate("payment_date"));
        System.out.println("Status       : " + rs.getString("status"));
        System.out.println("--------------------------------");
    }
}