import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Booking {
    private String bookingID;
    private Trip trip;
    private LocalDate date;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking(Trip trip) {
        this.bookingID = UUID.randomUUID().toString();
        this.trip = trip;
    }

    public Booking(String bookingID, Trip trip, LocalDate date) {
        this.bookingID = bookingID;
        this.trip = trip;
        this.date = date;
    }



    public String getBookingID() {
        return bookingID;
    }

    public Trip getTrip() {
        return trip;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking ID: ").append(bookingID).append("\n");
        sb.append("Departure Date: ").append(date).append("\n");
        sb.append("Trip Details:\n").append(trip.toString()).append("\n");
        sb.append("Reservations:\n");
        for (Reservation r : reservations) {
            sb.append(" - ").append(r).append("\n");
        }
        return sb.toString();
    }
}
