
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExchangeRateModel {

    public void addCurrency(String source, String destination, double rate, double fee) {
        String sql = "INSERT INTO ExchangeRates (source_currency, destination_currency, exchange_rate, fee) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE exchange_rate = VALUES(exchange_rate), fee = VALUES(fee)";
    
        DatabaseConnection obj = new DatabaseConnection();
    
        try (Connection connectDb = obj.getConnection()) {
            PreparedStatement stmt = connectDb.prepareStatement(sql);
            stmt.setString(1, source);
            stmt.setString(2, destination);
            stmt.setDouble(3, rate);
            stmt.setDouble(4, fee);
    
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting or updating exchange rate in database: " + e.getMessage());
        }
    }

 


    public double calculateExchangeRate(String source, String destination, double amount) {
        boolean rateFound = false;
        String sql = "SELECT source_currency, destination_currency, exchange_rate, fee FROM ExchangeRates";
        DatabaseConnection obj = new DatabaseConnection();
    
        try (Connection connectDb = obj.getConnection()) {
            PreparedStatement stmt = connectDb.prepareStatement(sql);
    
            // Create a graph of currencies and exchange rates
            Map<String, List<CurrencyNode>> graph = new HashMap<>();
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sourceCurrency = rs.getString("source_currency");
                    String destinationCurrency = rs.getString("destination_currency");
                    double exchangeRate = rs.getDouble("exchange_rate");
                    double fee = rs.getDouble("fee");
    
                    graph.putIfAbsent(sourceCurrency, new ArrayList<>());
                    graph.get(sourceCurrency).add(new CurrencyNode(destinationCurrency, exchangeRate, fee));
                }
            }
    
            // Use BFS to find the shortest path from the source currency to the destination currency
            Queue<CurrencyNode> queue = new LinkedList<>();
            queue.add(new CurrencyNode(source, 1, 0));
    
            while (!queue.isEmpty()) {
                CurrencyNode node = queue.poll();
    
                if (node.currency.equals(destination)) {
                    rateFound = true;
                    return amount * node.rate;
                }
    
                if (graph.containsKey(node.currency)) {
                    for (CurrencyNode neighbor : graph.get(node.currency)) {
                        queue.add(new CurrencyNode(neighbor.currency, node.rate * neighbor.rate, node.fee + neighbor.fee));
                    }
                }
            }
    
            if (!rateFound) {
                showAlert(source, destination);
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Error calculating exchange rate: " + e.getMessage());
            return 0;
        }
    }
    
    public double calculateTotalFee(String source, String destination, double amount) {
        boolean rateFound = false;
        String sql = "SELECT source_currency, destination_currency, exchange_rate, fee FROM ExchangeRates";
        DatabaseConnection obj = new DatabaseConnection();
    
        try (Connection connectDb = obj.getConnection()) {
            PreparedStatement stmt = connectDb.prepareStatement(sql);
    
            // Create a graph of currencies and exchange rates
            Map<String, List<CurrencyNode>> graph = new HashMap<>();
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sourceCurrency = rs.getString("source_currency");
                    String destinationCurrency = rs.getString("destination_currency");
                    double exchangeRate = rs.getDouble("exchange_rate");
                    double fee = rs.getDouble("fee");
    
                    graph.putIfAbsent(sourceCurrency, new ArrayList<>());
                    graph.get(sourceCurrency).add(new CurrencyNode(destinationCurrency, exchangeRate, fee));
                }
            }
    
            // Use BFS to find the shortest path from the source currency to the destination currency
            Queue<CurrencyNode> queue = new LinkedList<>();
            queue.add(new CurrencyNode(source, 1, 0));
            double totalFee = 0;
    
            while (!queue.isEmpty()) {
                CurrencyNode node = queue.poll();
    
                if (node.currency.equals(destination)) {
                    rateFound = true;
                    return totalFee;
                }
    
                if (graph.containsKey(node.currency)) {
                    for (CurrencyNode neighbor : graph.get(node.currency)) {
                        totalFee += amount * neighbor.fee;
                        queue.add(new CurrencyNode(neighbor.currency, node.rate * neighbor.rate, node.fee + neighbor.fee));
                    }
                }
            }

            if (!rateFound) {
                showAlert(source, destination);
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Error calculating total fee: " + e.getMessage());
            return 0;
        }
    }
    
    public void showAlert(String source, String destination) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No exchange rate found from " + source + " to " + destination);
            alert.showAndWait();
        });
    }
    
public String getActiveUserID() {
    String sql = "SELECT userID FROM users WHERE status = 'active'";

    DatabaseConnection obj = new DatabaseConnection();

    try (Connection connectDb = obj.getConnection();
         PreparedStatement stmt = connectDb.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            return rs.getString("userID");
        }
    } catch (SQLException e) {
        System.out.println("Error getting active user ID: " + e.getMessage());
    }

    return null;
}




public void exchangeCurrency(String userID, String sourceCurrency, String destinationCurrency, double amount) {
  
    String updateBalanceSql = "INSERT INTO balance (userID, currency, amount) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = amount + VALUES(amount)";
    String deductFeeSql = "UPDATE users SET balance = balance - ? WHERE userID = ?";

    DatabaseConnection obj = new DatabaseConnection();

    try (Connection connectDb = obj.getConnection();
         PreparedStatement updateBalanceStmt = connectDb.prepareStatement(updateBalanceSql);
         PreparedStatement deductFeeStmt = connectDb.prepareStatement(deductFeeSql)) {

        double exchangeRate = calculateExchangeRate(sourceCurrency, destinationCurrency, amount);
        double totalFee = calculateTotalFee(sourceCurrency, destinationCurrency, amount);

        updateBalanceStmt.setString(1, userID);
        updateBalanceStmt.setString(2, destinationCurrency);
        updateBalanceStmt.setDouble(3, exchangeRate);
        updateBalanceStmt.executeUpdate();

        deductFeeStmt.setDouble(1, totalFee+amount);
        deductFeeStmt.setString(2, userID);
       deductFeeStmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println("Error exchanging currency: " + e.getMessage());
    }
}

public double getUserBalance(String userID) {
    String sql = "SELECT balance FROM users WHERE userID = ?";
    DatabaseConnection obj = new DatabaseConnection();
    try (Connection connectDb = obj.getConnection();
         PreparedStatement stmt = connectDb.prepareStatement(sql)) {
        stmt.setString(1, userID);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
    } catch (SQLException e) {
        System.out.println("Error getting user balance: " + e.getMessage());
    }
    return 0;
}


public List<String> getUniqueCurrencies() {
    String sql = "SELECT DISTINCT source_currency FROM ExchangeRates UNION SELECT DISTINCT destination_currency FROM ExchangeRates";
    DatabaseConnection obj = new DatabaseConnection();
    List<String> currencies = new ArrayList<>();

    try (Connection connectDb = obj.getConnection()) {
        PreparedStatement stmt = connectDb.prepareStatement(sql);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                currencies.add(rs.getString(1));
            }
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving unique currencies: " + e.getMessage());
    }

    return currencies;
}
}   