import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDB {
    private List<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public Booking findBookingById(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingID().equals(bookingId)) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> getAllBookings() {
        return bookings;
    }

    public String generateBookingID() {
        return "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
