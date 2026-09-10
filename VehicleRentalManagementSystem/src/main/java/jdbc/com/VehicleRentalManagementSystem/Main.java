 package jdbc.com.VehicleRentalManagementSystem;

 import java.util.Scanner;

 public class Main {

     private static final Scanner sc = new Scanner(System.in);

     public static int readInt(String message) {
         while (true) {
             try {
                 System.out.print(message);
                 return Integer.parseInt(sc.nextLine());
             } catch (NumberFormatException e) {
                 System.out.println("Please enter a valid number.");
             }
         }
     }

     public static void main(String[] args) {

         while (true) {

             System.out.println("\n===== VEHICLE RENTAL SYSTEM =====");
             System.out.println("1. Admin Login");
             System.out.println("2. Customer Registration");
             System.out.println("3. Customer Login");
             System.out.println("4. Exit");

             int choice = readInt("Enter choice: ");

             switch (choice) {
                 case 1:
                     AdminOperation.adminLogin(sc);
                     break;
                 case 2:
                     CustomerOperation.registerCustomer(sc);
                     break;
                 case 3:
                     int customerId = CustomerOperation.customerLogin(sc);
                     if (customerId != -1) {
                         CustomerOperation.customerMenu(sc, customerId);
                     }
                     break;
                 case 4:
                     System.out.println("Thank you for using the system.");
                     ConnectionPool.shutdown();
                     sc.close();
                     return;
                 default:
                     System.out.println("Invalid choice. Please try again.");
             }
         }
     }
 }