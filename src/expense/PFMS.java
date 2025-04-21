package expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class PFMS {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "personal_finance";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "rohit@123"; // Update with your MySQL password

    private static Scanner scanner = new Scanner(System.in);
    private static Connection conn;
    private static String currentUsername;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            initializeDatabase();

            System.out.println("1. Create Account");
            System.out.println("2. Access Account");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter your username: ");
            String username = scanner.nextLine().toLowerCase().trim();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine().trim();
            currentUsername = username;

            if (choice == 1) {
                if (createUser(username, password)) {
                    System.out.println("Account created. Welcome, " + username + "!");
                    showMainMenu();
                } else {
                    System.out.println("Username already exists.");
                }
            } else if (choice == 2) {
                if (authenticateUser(username, password)) {
                    System.out.println("Login successful. Welcome back, " + username + "!");
                    showMainMenu();
                } else {
                    System.out.println("Invalid username or password.");
                }
            } else {
                System.out.println("Invalid choice.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);

        stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE, " +
                "password VARCHAR(255))");
    }

    private static boolean createUser(String username, String password) throws SQLException {
        if (checkUserExists(username)) return false;

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.executeUpdate();

        createUserTables(username);
        return true;
    }

    private static boolean authenticateUser(String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedPassword = rs.getString("password");
            return storedPassword.equals(password);
        }
        return false;
    }

    private static boolean checkUserExists(String username) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private static void createUserTables(String username) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS daily_expenses_" + username + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "date DATE, amount DOUBLE, category VARCHAR(50), description TEXT)");

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS travel_expenses_" + username + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "date DATE, amount DOUBLE, destination VARCHAR(100), purpose VARCHAR(100), description TEXT)");

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS medical_expenses_" + username + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "date DATE, amount DOUBLE, category VARCHAR(50), provider VARCHAR(100), description TEXT)");
    }

    private static void showMainMenu() throws SQLException {
        while (true) {
            System.out.println("\n1. Add Daily Expense");
            System.out.println("2. Add Travel Expense");
            System.out.println("3. Add Medical Expense");
            System.out.println("4. View Summary Report");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addDailyExpense();
                case 2 -> addTravelExpense();
                case 3 -> addMedicalExpense();
                case 4 -> viewSummaryMenu();
                case 5 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    
    private static void addDailyExpense() throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Category: ");
        String category = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO daily_expenses_" + currentUsername + " (date, amount, category, description) VALUES (?, ?, ?, ?)");
        stmt.setDate(1, Date.valueOf(date));
        stmt.setDouble(2, amount);
        stmt.setString(3, category);
        stmt.setString(4, desc);
        stmt.executeUpdate();
        System.out.println("Daily expense added.");
    }

    private static void addTravelExpense() throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Destination: ");
        String destination = scanner.nextLine();

        System.out.print("Purpose: ");
        String purpose = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO travel_expenses_" + currentUsername + " (date, amount, destination, purpose, description) VALUES (?, ?, ?, ?, ?)");
        stmt.setDate(1, Date.valueOf(date));
        stmt.setDouble(2, amount);
        stmt.setString(3, destination);
        stmt.setString(4, purpose);
        stmt.setString(5, desc);
        stmt.executeUpdate();
        System.out.println("Travel expense added.");
    }

    private static void addMedicalExpense() throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Category: ");
        String category = scanner.nextLine();

        System.out.print("Provider: ");
        String provider = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO medical_expenses_" + currentUsername + " (date, amount, category, provider, description) VALUES (?, ?, ?, ?, ?)");
        stmt.setDate(1, Date.valueOf(date));
        stmt.setDouble(2, amount);
        stmt.setString(3, category);
        stmt.setString(4, provider);
        stmt.setString(5, desc);
        stmt.executeUpdate();
        System.out.println("Medical expense added.");
    }

    private static void viewSummaryMenu() throws SQLException {
        System.out.println("\n1. Daily Summary");
        System.out.println("2. Weekly Summary");
        System.out.println("3. Monthly Summary");
        System.out.println("4. Yearly Summary");
        System.out.println("5. Specific Date Summary");
        System.out.println("6. Date Range Summary");
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> generateSummary("DAY", 1);
            case 2 -> generateSummary("WEEK", 7);
            case 3 -> generateSummary("MONTH", 30);
            case 4 -> generateSummary("YEAR", 365);
            case 5 -> checkSpecificDateExpenses();
            case 6 -> checkDateRangeExpenses();
            default -> System.out.println("Invalid choice.");
        }
    }
    
    private static void checkSpecificDateExpenses() throws SQLException {
        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        
        System.out.println("\nExpenses for " + date + ":");
        System.out.println("------------------------");
        
        double total = 0;
        total += printAndSumForDate("daily_expenses_" + currentUsername, "category", date);
        total += printAndSumForDate("travel_expenses_" + currentUsername, "destination", date);
        total += printAndSumForDate("medical_expenses_" + currentUsername, "category", date);
        
        System.out.println("------------------------");
        System.out.println("Total Expenses for " + date + ": Rs" + total);
    }

    private static void checkDateRangeExpenses() throws SQLException {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Enter end date (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        
        System.out.println("\nExpenses between " + startDate + " and " + endDate + ":");
        System.out.println("------------------------");
        
        double total = 0;
        total += printAndSumForDateRange("daily_expenses_" + currentUsername, "category", startDate, endDate);
        total += printAndSumForDateRange("travel_expenses_" + currentUsername, "destination", startDate, endDate);
        total += printAndSumForDateRange("medical_expenses_" + currentUsername, "category", startDate, endDate);
        
        System.out.println("------------------------");
        System.out.println("Total Expenses between " + startDate + " and " + endDate + ": Rs" + total);
    }

    private static double printAndSumForDate(String table, String field, LocalDate date) throws SQLException {
        String sql = "SELECT date, amount, " + field + " FROM " + table + " WHERE date = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(date));
        ResultSet rs = stmt.executeQuery();

        double sum = 0;
        while (rs.next()) {
            LocalDate expenseDate = rs.getDate("date").toLocalDate();
            double amount = rs.getDouble("amount");
            String fieldVal = rs.getString(field);
            System.out.printf("%s | %s | Rs%.2f\n", expenseDate, fieldVal, amount);
            sum += amount;
        }
        return sum;
    }

    private static double printAndSumForDateRange(String table, String field, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT date, amount, " + field + " FROM " + table + " WHERE date BETWEEN ? AND ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(startDate));
        stmt.setDate(2, Date.valueOf(endDate));
        ResultSet rs = stmt.executeQuery();

        double sum = 0;
        while (rs.next()) {
            LocalDate expenseDate = rs.getDate("date").toLocalDate();
            double amount = rs.getDouble("amount");
            String fieldVal = rs.getString(field);
            System.out.printf("%s | %s | Rs%.2f\n", expenseDate, fieldVal, amount);
            sum += amount;
        }
        return sum;
    }


    private static void generateSummary(String label, int days) throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate from = now.minusDays(days);

        double total = 0;
        total += printAndSum("daily_expenses_" + currentUsername, "category", from);
        total += printAndSum("travel_expenses_" + currentUsername, "destination", from);
        total += printAndSum("medical_expenses_" + currentUsername, "category", from);

        System.out.println("Total " + label + " Expenses: Rs" + total);
    }

    private static double printAndSum(String table, String field, LocalDate fromDate) throws SQLException {
        String sql = "SELECT date, amount, " + field + " FROM " + table + " WHERE date >= ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, Date.valueOf(fromDate));
        ResultSet rs = stmt.executeQuery();

        double sum = 0;
        while (rs.next()) {
            LocalDate date = rs.getDate("date").toLocalDate();
            double amount = rs.getDouble("amount");
            String fieldVal = rs.getString(field);
            System.out.printf("%s | %s | $%.2f\n", date, fieldVal, amount);
            sum += amount;
        }
        return sum;
    }
}