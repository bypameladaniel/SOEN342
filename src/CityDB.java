import java.util.HashMap;
import java.util.Map;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

/* Summary:
   - Add sqlite DB connection and ensure City table exists here.
   - Implement loadCitiesFromDB() to load all City rows into the in-memory map.
*/

public class CityDB {
    private Map<String, City> cities = new HashMap<>();
    private Connection dbConnection;

    public CityDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createCityTableIfNotExist();
            loadCitiesFromDB(); // populate in-memory map at startup
            System.out.println("✅ SQLite connected in CityDB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Could not connect to database in CityDB.");
            e.printStackTrace();
        }
    }

    private void createCityTableIfNotExist() throws SQLException {
        String cityTable = """
            CREATE TABLE IF NOT EXISTS City (
                name TEXT PRIMARY KEY
            );
        """;
        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute(cityTable);
        }
    }

    // New: load all cities from the DB into the in-memory map
    public void loadCitiesFromDB() {
        String query = "SELECT name FROM City";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            cities.clear();
            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null && !name.isEmpty()) {
                    City city = new City(name);
                    cities.put(name, city);
                }
            }
            System.out.println("✅ Loaded " + cities.size() + " cities from DB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Failed to load cities from DB.");
            e.printStackTrace();
        }
    }

    public void addCity(City city) {
        cities.put(city.getName(), city);
        String sql = "INSERT OR REPLACE INTO City (name) VALUES (?)";
        try (var stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, city.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Failed to save city to DB: " + city.getName());
            e.printStackTrace();
        }
    }



    public City findCity(String name) {
        return cities.get(name); // must match capitalization (e.g., "Paris")
    }

}
