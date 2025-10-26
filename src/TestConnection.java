import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/id_card_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "root";
        String password = "Root";

        System.out.println("Testing MySQL connection...");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver loaded successfully");

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connection successful!");
            System.out.println("✓ Connected to: " + conn.getCatalog());

            conn.close();
            System.out.println("✓ Connection closed successfully");

        } catch (Exception e) {
            System.out.println("✗ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
