import java.util.ArrayList;
import java.util.List;

public class Booking {
    private String bookingID;
    private Trip trip;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking(String bookingID, Trip trip) {
        this.bookingID = bookingID;
        this.trip = trip;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public String getBookingID() {
        return bookingID;
    }

    public Trip getTrip() {
        return trip;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking ID: ").append(bookingID).append("\n");
        sb.append("Trip Details:\n").append(trip.toString()).append("\n");
        sb.append("Reservations:\n");
        for (Reservation r : reservations) {
            sb.append(" - ").append(r).append("\n");
        }
        return sb.toString();
    }
}
