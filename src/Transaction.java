

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction{
    private String transactionID;
    private String userID;
    private String recipientID;
    public double amount;
    public double balance;
    public Timestamp date;
    public String category;
    public String payment_method;

    public Transaction(){
        this.transactionID=null;
        this.userID=null;
        this.recipientID=null;
        this.amount=0.00;
        this.balance=0.00;
        this.date=null;
        this.category=null;
        this.payment_method=null;
    }

    public Transaction(String userID, String recipientID, double amount, double balance, String category){
        this.transactionID=generateTransactionID();
        this.userID=userID;
        this.recipientID=recipientID;
        this.amount=amount;
        this.balance=balance;
        this.date=generateCurrentDateTime();
        this.category=category;
    }

    public Transaction(String transactionID, String userID, String recipientID, double amount, double balance, Timestamp date, String category) {
        this.transactionID = transactionID;
        this.userID = userID;
        this.recipientID = recipientID;
        this.amount = amount;
        this.balance = balance;
        this.date = date;
        this.category = category;
    }

    public void setTransactionID(String transactionID){
        this.transactionID=transactionID;
    }
    public void setUserID(String userID){
        this.userID=userID;
    }
    public void setRecipientID(String recipientID){
        this.recipientID=recipientID;
    }
    public void setAmount(double amount){
        this.amount=amount;
    }
    public void setBalance(double balance){
        this.balance=balance;
    }
    public void setDate(Timestamp dateOfTrans){
        this.date=dateOfTrans;
    }
    public void setCategory(String category){
        this.category=category;
    }
    public void setPaymentMethod(String payment_method){
        this.payment_method=payment_method;
    }

    public String getTransactionID(){
        return this.transactionID;
    }
    public String getUserID(){
        return this.userID;
    }
    public String getRecipientID(){
        return this.recipientID;
    }
    public double getAmount(){
        return this.amount;
    }
    public double getBalance(){
        return this.balance;
    }
    public Timestamp getDate(){
        return this.date;
    }
    public String getCategory(){
        return this.category;
    }
    public String getPaymentMethod(){
        return this.payment_method;
    }
    public String generateTransactionID() {
        StringBuilder sb = new StringBuilder();
        while(sb.length() < 6) {
            if(sb.length() > 3) {
                int num = (int)(Math.random()*10);//generater random number from 0-9
                sb.append(num);
            } else {
                char letter = (char)(Math.random()*26 + 'a');//generater random letter from a-z
                sb.append(letter);
            }
        }
        return sb.toString();
    }

    public Timestamp generateCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Timestamp.valueOf(currentDateTime);
    }

}