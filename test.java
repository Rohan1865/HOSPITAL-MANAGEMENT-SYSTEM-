import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC";
        String username = "root";  // Replace with your MySQL username
        String password = "your_password";  // Replace with your MySQL password

        try {
            // Load MySQL Connector/J 9.1.0 Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to MySQL
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully!");

            // Use the connection (e.g., query the database)
            // ...

            // Close the connection when done
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed. Check your MySQL credentials and URL.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Ensure the connector JAR is in your classpath.");
            e.printStackTrace();
        }
    }
}
