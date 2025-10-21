import java.util.ArrayList;
import java.util.List;

public class Client {
    private String clientID;
    private String name;
    private int age;
    private List<Booking> bookings = new ArrayList<>();

    public Client(String clientID, String name, int age) {
        this.clientID = clientID;
        this.name = name;
        this.age = age;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public String getClientID() {
        return clientID;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    @Override
    public String toString() {
        return String.format("Client[%s] %s (Age: %d)", clientID, name, age);
    }
}
