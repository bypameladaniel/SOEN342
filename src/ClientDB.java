import java.sql.*;
import java.util.*;

public class ClientDB {
    private Map<Long, Client> clients = new HashMap<>();
    private java.sql.Connection dbConnection;

    public ClientDB(BookingDB bookingDB) {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
            loadClientsFromDB();
            loadClientBookingsFromDB(bookingDB);// populate map on startup
            System.out.println("✅ SQLite connected in ClientDB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Could not connect to database in ClientDB.");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Client (
                clientId INTEGER PRIMARY KEY,
                firstName TEXT,
                lastName TEXT,
                age INTEGER
            );
        """;

        String sql2 = """
            CREATE TABLE IF NOT EXISTS Client_Booking ( 
                clientId INTEGER,
                bookingId TEXT,
                previous BOOLEAN,
                primary key (clientId, bookingId),
                foreign key (clientId) references Client(clientId),
                foreign key (bookingId) references Booking(bookingId)  
            );
                """;

        dbConnection.createStatement().execute(sql);
        dbConnection.createStatement().execute(sql2);
    }

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

    public void addClientBooking(Long clientId, String bookingId) {
        String sql = "INSERT OR REPLACE INTO Client_Booking (clientId, bookingId, previous) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            stmt.setString(2, bookingId);
            stmt.setBoolean(3, false);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markBookingAsPrevious(Long clientID, String bookingID) {
        String sql = "UPDATE Client_Booking SET previous = ? WHERE clientId = ? AND bookingId = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setBoolean(1, true);
            stmt.setLong(2, clientID);
            stmt.setString(3, bookingID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClientBookingsFromDB(BookingDB bookingDB) {
        String sql = "SELECT clientId, bookingId, previous FROM Client_Booking";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long clientId = rs.getLong("clientId");
                String bookingId = rs.getString("bookingId");
                boolean previous = rs.getBoolean("previous");

                Client client = clients.get(clientId);


                Booking booking = bookingDB.findBookingById(bookingId);
                if (client != null && booking != null) {
                    if (previous) {
                        client.addPreviousBooking(booking);;
                    } else {
                        client.addBooking(booking);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
