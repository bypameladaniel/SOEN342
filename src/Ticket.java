import java.util.UUID;

public class Ticket {
    private String ticketID;

    public Ticket() {
        this.ticketID = generateTicketID();
    }

    public Ticket(String ticketID) {
        this.ticketID = ticketID;
    }

    private String generateTicketID() {
        return "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getTicketID() {
        return ticketID;
    }

    @Override
    public String toString() {
        return ticketID;
    }
}
