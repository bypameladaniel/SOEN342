import java.sql.*;
import java.util.*;

public class ReservationDB {
    private final Map<Long, Reservation> reservations = new HashMap<>();
    private java.sql.Connection dbConnection;

    private ClientDB clientDB;
    private BookingDB bookingDB;
    private TicketDB ticketDB;


    public ReservationDB(ClientDB clientDB, BookingDB bookingDB, TicketDB ticketDB) {

        this.clientDB = clientDB;
        this.bookingDB = bookingDB;
        this.ticketDB = ticketDB;



        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
            loadReservationsFromDB();
            System.out.println("✅ SQLite connected in ReservationDB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Could not connect to database in ReservationDB.");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Reservation (
                reservationId INTEGER PRIMARY KEY AUTOINCREMENT,
                clientId INTEGER,
                bookingId TEXT,
                ticketId TEXT,
                foreign key (clientId) references Client(clientId),
                foreign key (bookingId) references Booking(bookingId),
                foreign key (ticketId) references Ticket(ticketId)
            );
        """;
        dbConnection.createStatement().execute(sql);
    }

    private void loadReservationsFromDB() {
        String query = "SELECT reservationId, clientId, bookingId, ticketId FROM Reservation";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            reservations.clear();
            while (rs.next()) {
                long reservationId = rs.getLong("reservationId");

                Long clientId = rs.getLong("clientId");
                if (rs.wasNull()) clientId = null;

                String bookingId = rs.getString("bookingId");
                if (rs.wasNull()) bookingId = null;

                String ticketId = rs.getString("ticketId");
                if (rs.wasNull()) ticketId = null;

                Client client = clientId != null ? clientDB.findClientById(clientId) : null;
                Booking booking = bookingId != null ? bookingDB.findBookingById(bookingId) : null;
                Ticket ticket = ticketId != null ? ticketDB.findTicket(ticketId) : null;


                Reservation r = new Reservation(client, booking, ticket);
                if (booking != null){
                    booking.addReservation(r);
                }
                reservations.put(reservationId, r);
            }
            System.out.println("📋 Loaded " + reservations.size() + " reservations from DB.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long addReservation(Reservation reservation) {


         long generatedId = -1;

        String insert = "INSERT INTO Reservation (clientId, bookingId, ticketId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = dbConnection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            Long clientId = reservation.getClient() != null ? reservation.getClient().getClientID() : null;
            String bookingId = reservation.getBooking() != null ? reservation.getBooking().getBookingID() : null;
            String ticketId = reservation.getTicket() != null ? reservation.getTicket().getTicketID() : null;

            if (clientId != null) ps.setLong(1, clientId); else ps.setNull(1, Types.BIGINT);
            if (bookingId != null) ps.setString(2, bookingId); else ps.setNull(2, Types.BIGINT);
            if (ticketId != null) ps.setString(3, ticketId); else ps.setNull(3, Types.BIGINT);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    reservation.setReservationID(keys.getLong(1)) ;
                    reservations.put(reservation.getReservationID(), reservation);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Reservation findReservationById(Long reservationId) {
        return reservations.get(reservationId);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }

    public List<Reservation> findReservationsByClientId(Long clientId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations.values()) {
            Client c = r.getClient();
            if (c != null && c.getClientID() == clientId) {
                result.add(r);
            }
        }
        return result;
    }
}