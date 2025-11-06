import java.sql.*;
import java.util.*;

public class ClientDB {
    private Map<Long, Client> clients = new HashMap<>();
    private java.sql.Connection dbConnection;

    public ClientDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
            loadClientsFromDB(); // populate map on startup
            System.out.println("✅ SQLite connected in ClientDB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Could not connect to database in ClientDB.");
            e.printStackTrace();
        }
    }

    /** Create table if it doesn't exist */
    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Client (
                clientId INTEGER PRIMARY KEY,
                firstName TEXT,
                lastName TEXT,
                age INTEGER
            );
        """;
        dbConnection.createStatement().execute(sql);
    }

    /** Add to map and persist in DB */
    public void addClient(Client client) {
        clients.put(client.getClientID(), client);

        String sql = "INSERT OR REPLACE INTO Client (clientId, firstName, lastName, age) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setLong(1, client.getClientID());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getLastName());
            stmt.setInt(4, client.getAge());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Load existing clients into memory at startup */
    private void loadClientsFromDB() {
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Client")) {

            while (rs.next()) {
                long id = rs.getLong("clientId");
                String first = rs.getString("firstName");
                String last = rs.getString("lastName");
                int age = rs.getInt("age");

                Client c = new Client(id, first, last, age);
                clients.put(id, c);
            }

            System.out.println("📋 Loaded " + clients.size() + " clients from DB.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Client findClientById(Long clientID) {
        return clients.get(clientID);
    }

    public Client finClientByIDAndName(Long clientID, String name) {
        Client client = clients.get(clientID);
        if (client != null && client.getLastName() != null && client.getLastName().equalsIgnoreCase(name)) {
            return client;
        }
        return null;
    }

    public Client findClientByLastName(String name) {
        for (Client c : clients.values()) {
            if (c.getLastName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }
}
