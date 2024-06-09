import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class divinationModel {

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
    
    
    String activeUserId =  getActiveUserID();


    // Get the total expenditure of the active user
    public Map<String, Double> getCategoryExpenditure(String activeUserId) {
        
        String query = "SELECT category, amount FROM transactions3 WHERE userID = ?";

        Map<String, Double> categoryExpenditureMap = new HashMap<>();

        DatabaseConnection obj = new DatabaseConnection();

            try (Connection connectDb = obj.getConnection();
             PreparedStatement stmt = connectDb.prepareStatement(query)) {

            stmt.setString(1, activeUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                categoryExpenditureMap.put(category, categoryExpenditureMap.getOrDefault(category, 0.0) + amount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoryExpenditureMap;
    }

    // Get the total expenditure of the active user
    public Map<String, Double> getFilteredExpenditure(String activeUserId, LocalDate date, String category, String monthly, String paymentMethod) {
        Map<String, Double> expenditureData = new HashMap<>();

        DatabaseConnection obj = new DatabaseConnection();
    // filtered by date, category, monthly, payment method
            try (Connection connectDb = obj.getConnection()) {
            StringBuilder query = new StringBuilder("SELECT category, SUM(amount) FROM transactions3 WHERE userID = ?");

            if (date != null) {
                query.append(" AND DATE(date) = ?");
            }
            if (category != null) {
                query.append(" AND category = ?");
            }
            if (monthly != null) {
                query.append(" AND MONTH(date) = ?");
            }
            if (paymentMethod != null) {
                query.append(" AND payment_method = ?");
            }

            query.append(" GROUP BY category");

            try (PreparedStatement statement = connectDb.prepareStatement(query.toString())) {
                statement.setString(1, activeUserId);

                int index = 2;
                if (date != null) {
                    statement.setDate(index++, java.sql.Date.valueOf(date));
                }
                if (category != null) {
                    statement.setString(index++, category);
                }
                if (monthly != null) {
                    statement.setInt(index++, Integer.parseInt(monthly));
                }
                if (paymentMethod != null) {
                    statement.setString(index++, paymentMethod);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        expenditureData.put(resultSet.getString(1), resultSet.getDouble(2));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return expenditureData;
    }
}


