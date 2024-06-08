import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Base64;

public class SecurePin {
    private boolean correctPin;

    public SecurePin() {
        this.correctPin = false;
    }

    //setter
    public void setCorrectPin(boolean correctPin) {this.correctPin = correctPin;}

    //getter
    public boolean getCorrectPin() {return this.correctPin;}

    //pin encryption
    public String encryptPin(String pin){
        byte[] encodedPin = Base64.getEncoder().encode(pin.getBytes());

        StringBuilder sb = new StringBuilder();
        for(byte b : encodedPin){
            sb.append((char)b);
        }

        String encryptedPin = sb.toString();
        return encryptedPin;
    }//end of encryptPin

    //pin decryption
    public int decryptPin(String encryptedPin){
        byte[] decodedPin = Base64.getDecoder().decode(encryptedPin.getBytes());
        int decryptedPin = Integer.parseInt(new String(decodedPin));
        
        return decryptedPin;
    }//end of decryptPin

    public boolean validatePin(int pin, String userID) {
    DatabaseConnection2 connectNow = new DatabaseConnection2();
    Connection connectDB = connectNow.getConnection();

    String verifyPinQuery = "SELECT pin FROM Users WHERE userID = ?";
    
    try {
        PreparedStatement preparedStatement = connectDB.prepareStatement(verifyPinQuery);
        preparedStatement.setString(1, userID);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            String encryptedPin = rs.getString("pin");// Get the encrypted pin from the database
            int decryptedPin = decryptPin(encryptedPin);// Decrypt the pin from the database

            // Compare the decrypted pin with the user-entered pin
            if (pin == decryptedPin) {
                return true;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } 

    return false;
}

}