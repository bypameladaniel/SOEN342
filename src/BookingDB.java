import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookingDB {
    private java.sql.Connection dbConnection;
    private HashMap<String, Booking> bookings = new HashMap<>();


    public BookingDB(TripDB tripDB) {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
            loadBookingFromDB(tripDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Booking (
                bookingId TEXT PRIMARY KEY,
                tripId INTEGER,
                date TEXT,
                foreign key (tripId) references Trip(tripId)
            );
        """;


        dbConnection.createStatement().execute(sql);
    }

    public void addBooking(Booking booking) {


        String sql = "INSERT INTO Booking (bookingId, tripId, date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, booking.getBookingID());
            stmt.setString(3, booking.getDate() != null ? booking.getDate().toString() : null);
            stmt.setLong(2, booking.getTrip().getTripId());

            bookings.put(booking.getBookingID(), booking);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadBookingFromDB(TripDB tripDB) {
        String sql = "SELECT * FROM Booking";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking(rs.getString("bookingId"),tripDB.findTripById(rs.getLong("tripId")), LocalDate.parse(rs.getString("date")) );
                bookings.put(b.getBookingID(), b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Booking findBookingById(String bookingId) {
        return bookings.get(bookingId);
    }
}

