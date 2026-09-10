Vehicle Rental Management System developed using Core Java, JDBC, and MySQL. The application provides separate Admin and Customer modules to manage vehicles, customers, rentals, returns, payments, and reports.

📌 Project Overview

The Vehicle Rental Management System is designed to simplify vehicle rental operations through a menu-driven console application.

The system allows administrators to manage customers and vehicles, while customers can search available vehicles, rent vehicles, return rented vehicles, and manage payments.

🛠️ Technologies Used
Java – Core Java, OOP, Collections, Exception Handling
JDBC – Database connectivity and SQL operations
MySQL – Database management
SQL – CRUD operations, joins, queries, transactions
Eclipse / IntelliJ IDEA – Development environment
👨‍💼 Admin Module

The Admin module provides functionality for:

Admin Management
Admin Login
Logout
Change Password
Customer Management
View All Customers
Search Customer by ID
Search Customer by Phone
Update Customer Details
Delete Customer
Vehicle Management
Add Vehicle
View All Vehicles
Search Vehicle
Update Vehicle
Delete Vehicle
View Available Vehicles
View Rented Vehicles
Send Vehicle for Maintenance
Rental Management
View All Rentals
Search Rental by ID
View Active Rentals
View Completed Rentals
Payment Management
View Payments
View Pending Payments
View Completed Payments
Reports
Available Vehicles Report
Rented Vehicles Report
Rental History
Revenue Report
Most Rented Vehicles
👤 Customer Module

The Customer module provides:

Customer Registration
Customer Login
Logout
View Available Vehicles
Search Vehicle by Type
Search Vehicle by Brand
Search Vehicle by Rent Range
Rent a Vehicle
Return a Vehicle
View Rental History
View Payment Details
View Bill
Make Payment
🚗 Rental Process

The basic rental workflow is:

Customer Login
      ↓
View Available Vehicles
      ↓
Select Vehicle
      ↓
Enter Rental Dates
      ↓
Calculate Rental Amount
      ↓
Create Rental
      ↓
Vehicle Status → RENTED
🔄 Vehicle Return Process
Customer Selects Active Rental
            ↓
       Return Vehicle
            ↓
   Calculate Rental Amount
            ↓
     Calculate Late Fee
            ↓
Rental Status → COMPLETED
            ↓
Vehicle Status → AVAILABLE
            ↓
Create Payment Record
💳 Payment Process

Supported payment methods:

CASH
UPI
CARD

Payment flow:

Pending Payment
      ↓
Select Payment
      ↓
Choose Payment Method
      ↓
Payment Status → PAID
🗄️ Database

Database name:

vehicle_rental_management_system

Main tables:

Admin
Customer
Vehicle
Rental
Payment
🔌 JDBC Features

This project demonstrates important JDBC concepts:

Connection
PreparedStatement
CallableStatement
ResultSet
Transactions
Commit and Rollback
Connection Pooling
Stored Procedures
Exception Handling
🔐 Security

The project uses PreparedStatement for database operations to reduce SQL injection risks.

Note: Database passwords and other sensitive credentials should not be committed to GitHub.

📂 Project Structure
VehicleRentalManagementSystem/
│
├── src/
│   └── jdbc/
│       └── VehicleRentalManagementSystem/
│           ├── Main.java
│           ├── DBConnection.java
│           ├── ConnectionPool.java
│           ├── AdminOperation.java
│           ├── CustomerOperation.java
│           ├── VehicleOperation.java
│           ├── RentalOperation.java
│           ├── ReturnOperation.java
│           ├── PaymentOperation.java
│           └── ReportOperation.java
│
├── database/
│   └── vehicle_rental_management_system.sql
│
├── lib/
│   └── mysql-connector-j.jar
│
├── README.md
└── .gitignore
▶️ How to Run
1. Clone the Repository
git clone https://github.com/ADHINARAYANAN2004/VehicleRentalManagementSystem.git
2. Create the Database

Open MySQL and create:

CREATE DATABASE vehicle_rental_management_system;

Execute the SQL script available in the database folder.

3. Configure Database Connection

Update the database URL, username, and password in DBConnection.java.

Example:

String url = "jdbc:mysql://localhost:3306/vehicle_rental_management_system";
String username = "root";
String password = "your_password";

Do not upload your actual password to GitHub.

4. Add MySQL JDBC Driver

Add the MySQL Connector/J JAR to the project classpath if you are not using Maven.

5. Run the Application

Run:

Main.java
🎯 Key Features
Admin and Customer authentication
Vehicle management
Vehicle search
Vehicle rental
Vehicle return
Late fee calculation
Payment management
Rental history
Reports
JDBC transactions
Connection pooling
MySQL database integration
📚 Learning Outcomes

This project provides practical experience in:

Core Java
Object-Oriented Programming
JDBC
SQL and MySQL
CRUD operations
Exception Handling
Transactions
PreparedStatement
Stored Procedures
Connection Pooling
Console-based application development
👨‍💻 Developer

S. Adhinarayanan

B.Tech – Artificial Intelligence and Data Science

GitHub: ADHINARAYANAN2004
