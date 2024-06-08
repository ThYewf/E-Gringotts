public class User <T>{
    private String userID;//VARCHAR（25）PRIMARY KEY NOT NULL UNIQUE
    private String first_name;//VARCHAR（25）NOT NULL
    private String last_name;//VARCHAR（25）NOT NULL
    private String password;//VARCHAR(25) NOT NULL
    private String address;//VARCHAR(50) NOT NULL
    private String phone;//VARCHAR(15) NOT NULL
    private int pin;//VARCHAR(25) NOT NULL, encrypted so not int
    private String account;//VARCHAR(16) NOT NULL, unique
    private double balance;//DECIMAL, can be null since user can have 0 balance
    private UserTier tier;//ENUM('GOBLIN','SILVER','GOLDEN','PLATINUM') NOT NULL
    private String date;//DATE NOT NULL
    private Status status;//VARCHAR(25) NOT NULL

    //use by admin to create user constructor
    public User(String userID,String first_name ,String last_name ,String password, String address, String phone, int pin, String account, double balance, UserTier tier) {
        this.userID = userID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.pin = pin;
        this.account = account;
        this.balance = balance;
        this.tier = UserTier.GOBLIN;
    }

    //use by user to create user constructor
    public User(String userID,String first_name,String last_name, String password, String address, String phone, int pin, String account, double balance, UserTier tier, String date, Status status) {
        this.userID = userID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.pin = pin;
        this.account = account;
        this.balance = balance;
        this.tier = setUserTier();
        this.date = date;
        this.status = status;
    }

    //use for transaction constructor
    public User(String userID, String first_name, String last_name, String phone, double balance) {
        this.userID = userID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.balance = balance;
    }

    //default constructor
    public User() {
        this.userID = "";
        this.first_name = "";
        this.last_name = "";
        this.password = "";
        this.address = "";
        this.phone = "";
        this.pin = 0;
        this.account = "";
        this.tier = null;
        this.date = null;
        this.status = Status.INACTIVE;
    }

    //getter
    public String getUserID() {return this.userID;}
    public String getFirst_name() {return this.first_name;} 
    public String getLast_name() {return this.last_name;}
    public String getPassword() {return this.password;}
    public String getAddress() {return this.address;}
    public String getPhone() {return this.phone;}
    public int getPin() {return this.pin;}
    public String getAccount() {return this.account;}
    public double getBalance() {return this.balance;}
    public UserTier getTier() {return this.tier;}
    public String getDate() {return this.date;}
    public Status getStatus() {return this.status;}

    //setter
    public void setUserID(String userID) {this.userID = userID;}
    public void setFirst_name(String first_name) {this.first_name = first_name;}
    public void setLast_name(String last_name) {this.last_name = last_name;}
    public void setPassword(String password) {this.password = password;}
    public void setAddress(String address) {this.address = address;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setPin(int pin) {this.pin = pin;}
    public void setAccount(String account) {this.account = account;}
    public void setBalance(double balance) {this.balance = balance;}
    public void setTier(UserTier tier) {this.tier = tier;}
    public void setDate(String date) {this.date = date;}
    public void setStatus(Status status) {this.status = status;}

    //user tier set automaticaly based on user balance
    public UserTier setUserTier() {
        if(this.balance > 70000.0) 
            return UserTier.PLATINUM;
        else if (this.balance > 30000.0)
            return UserTier.GOLDEN;
        else 
            return UserTier.SILVER;
    }//end of setTier

    //automaticaly generate account number for new user
    public String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        while(sb.length() < 16) {
            int num = (int)(Math.random()*10);//generater random number from 0-9
            sb.append(num);
        }

        return sb.toString();
    }//end of generateAccountNumber

    
}