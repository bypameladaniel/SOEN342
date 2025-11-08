import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client {
    private Long clientID;
    private String firstName;
    private String lastName;
    private int age;
    private List<Booking> currentBookings = new ArrayList<>();
    private List<Booking> previousBookings = new ArrayList<>();

    public Client(Long clientID, String firstName, String lastName, int age) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public void addBooking(Booking booking) {
        currentBookings.add(booking);
    }

    public void addPreviousBooking(Booking booking) {
        previousBookings.add(booking);
    }

    public Long getClientID() {
        return clientID;
    }

    public String getFirstName() {return firstName;}

    public String getLastName() {return lastName;}

    public int getAge() {
        return age;
    }

    public List<Booking> getBookings() {
        return currentBookings;
    }

    public List<Booking> getPreviousBookings() {
        return previousBookings;
    }

    public void updateBookings(ClientDB clientDB) {

        Iterator<Booking> iterator = currentBookings.iterator();

        while (iterator.hasNext()) {
            Booking b = iterator.next();
            if (b.getDate().isBefore(LocalDate.now())) {
                previousBookings.add(b);
                iterator.remove();
                clientDB.markBookingAsPrevious(clientID, b.getBookingID());
            }
        }
    }

    public String getBookingSummary(ClientDB clientDB) {

        updateBookings(clientDB);

        StringBuilder sb = new StringBuilder();
        sb.append("Current Trips for ").append(firstName).append(":\n");
        for (Booking b : currentBookings) {
            if (b.getTrip() == null) {
                continue;
            }
            sb.append(b.getTrip().toString()).append("\n");
        }
        sb.append("Previous Trips for ").append(firstName).append(":\n");
        for (Booking b : previousBookings) {
            sb.append(b.getTrip().toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Client[%s] %s (Age: %d)", clientID, firstName + lastName, age);
    }
}
