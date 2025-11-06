import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDB {
    private java.sql.Connection dbConnection;

    public BookingDB() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Booking (
                bookingId TEXT PRIMARY KEY,
                date TEXT,
                tripDetails TEXT
            );
        """;
        dbConnection.createStatement().execute(sql);
    }

    public void addBooking(Booking booking) {
        String sql = "INSERT INTO Booking (bookingId, date, tripDetails) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, booking.getBookingID());
            stmt.setString(2, booking.getDate() != null ? booking.getDate().toString() : null);
            stmt.setString(3, booking.getTrip().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking(null);
                b.setDate(LocalDate.parse(rs.getString("date")));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
}
