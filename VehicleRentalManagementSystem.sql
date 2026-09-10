CREATE DATABASE  vehicle_rental_management_system;
USE vehicle_rental_management_system;

CREATE TABLE Admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE Customer (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE Vehicle (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_number BIGINT UNIQUE NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(30) NOT NULL,
    rent_per_day DECIMAL(10,2) NOT NULL,
    status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE') DEFAULT 'AVAILABLE'
);

CREATE TABLE Rental (
    rental_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    rental_date DATE NOT NULL,
    return_date DATE NOT NULL,
    total_amount DECIMAL(10,2),
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id)
);

CREATE TABLE Payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    rental_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method ENUM('CASH', 'UPI', 'CARD'),
    status ENUM('PAID', 'PENDING') DEFAULT 'PENDING',
    FOREIGN KEY (rental_id) REFERENCES Rental(rental_id)
);

INSERT INTO Admin (username, password) VALUES ('adhi', 'adhi123');

INSERT INTO Vehicle (vehicle_number, brand, model, vehicle_type, rent_per_day, status) VALUES
(1001, 'Maruti', 'Swift', 'Car', 1500.00, 'AVAILABLE'),
(1002, 'Honda', 'City', 'Car', 2000.00, 'AVAILABLE'),
(1003, 'Toyota', 'Innova', 'SUV', 3000.00, 'AVAILABLE'),
(1004, 'Hyundai', 'Creta', 'SUV', 2500.00, 'AVAILABLE'),
(1005, 'Honda', 'Activa', 'Scooter', 500.00, 'AVAILABLE'),
(1006, 'Bajaj', 'Pulsar', 'Bike', 800.00, 'AVAILABLE'),
(1007, 'Royal Enfield', 'Classic 350', 'Bike', 1200.00, 'AVAILABLE'),
(1008, 'TVS', 'Jupiter', 'Scooter', 450.00, 'AVAILABLE');