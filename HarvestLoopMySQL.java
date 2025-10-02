import java.sql.*;
import java.util.Scanner;

public class HarvestLoopMySQL {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/harvestloop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";               // Your MySQL username
        String password = "Urv@2509";       // Your MySQL password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ Connected to MySQL");

                // 1. Create tables if not exist
                String farmerTable = "CREATE TABLE IF NOT EXISTS farmer (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100)," +
                        "phone VARCHAR(20)," +
                        "village VARCHAR(100)," +
                        "language VARCHAR(50)" +
                        ")";
                conn.createStatement().execute(farmerTable);

                String listingTable = "CREATE TABLE IF NOT EXISTS listing (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "farmer_id INT," +
                        "title VARCHAR(200)," +
                        "material_type VARCHAR(100)," +
                        "quantity INT," +
                        "price_per_unit DOUBLE," +
                        "location VARCHAR(200)," +
                        "FOREIGN KEY(farmer_id) REFERENCES farmer(id) ON DELETE CASCADE" +
                        ")";
                conn.createStatement().execute(listingTable);

                // 2. Get farmer details from user
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter farmer name: ");
                String farmerName = sc.nextLine();
                System.out.print("Enter farmer phone: ");
                String farmerPhone = sc.nextLine();
                System.out.print("Enter farmer village: ");
                String farmerVillage = sc.nextLine();
                System.out.print("Enter farmer language: ");
                String farmerLanguage = sc.nextLine();

                // 3. Insert the farmer
                String insertFarmer = "INSERT INTO farmer(name, phone, village, language) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insertFarmer, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, farmerName);
                ps.setString(2, farmerPhone);
                ps.setString(3, farmerVillage);
                ps.setString(4, farmerLanguage);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                int farmerId = 0;
                if (keys.next()) farmerId = keys.getInt(1);
                System.out.println("👨‍🌾 Farmer inserted with ID: " + farmerId);

                // 4. Insert a listing (still hardcoded)
                String insertListing = "INSERT INTO listing(farmer_id, title, material_type, quantity, price_per_unit, location) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps2 = conn.prepareStatement(insertListing);
                ps2.setInt(1, farmerId);
                ps2.setString(2, "Leftover wheat stalks");
                ps2.setString(3, "Stalks");
                ps2.setInt(4, 100);
                ps2.setDouble(5, 2.5);
                ps2.setString(6, "Nagpur Market");
                ps2.executeUpdate();
                System.out.println("🌾 Listing inserted!");

                // 5. Display farmers
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM farmer");
                System.out.println("\n--- Farmers ---");
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + " | " +
                                       rs.getString("name") + " | " +
                                       rs.getString("phone") + " | " +
                                       rs.getString("village") + " | " +
                                       rs.getString("language"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
