import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TicketDB {
    private Map<String, Ticket> tickets;
    java.sql.Connection dbConnection;


    public TicketDB() {
        this.tickets = new HashMap<>();

        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
            getTicketFromDB();
            System.out.println("✅ SQLite connected in TicketDB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Could not connect to database in TicketDB.");
            e.printStackTrace();
        }

    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Ticket (
                ticketId TEXT PRIMARY KEY
            );
        """;
        dbConnection.createStatement().execute(sql);
    }

    private Ticket getTicketFromDB() {
        String query = "SELECT ticketID FROM Ticket";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tickets.clear();
            while (rs.next()) {
                String ticketID = rs.getString("ticketID");
                Ticket ticket = new Ticket();
                tickets.put(ticketID, ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void addTicket(Ticket ticket) {
        tickets.put(ticket.getTicketID(), ticket);

        String sql = "INSERT INTO Ticket (ticketID) VALUES (?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTicketID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Ticket findTicket(String ticketId) {
        return tickets.get(ticketId);
    }

    public Map<String, Ticket> getAllTickets() {
        return tickets;
    }


}
