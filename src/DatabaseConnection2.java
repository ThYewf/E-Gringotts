import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection2 {
    public final String CONNECITON = "jdbc:mysql://localhost:3306/user";
    public final String username = "root";
    public final String password = "wia1002";
    public Connection databaseLink;

    public Connection getConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(CONNECITON, username, password);

        }catch(Exception e){
            e.printStackTrace();
        }

        return databaseLink;
    }
}