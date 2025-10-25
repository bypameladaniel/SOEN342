import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TicketDB {
    private Map<String, Ticket> tickets;

    public TicketDB() {
        this.tickets = new HashMap<>();
    }

    public void addTicket(Ticket ticket) {
        tickets.put(ticket.getTicketID(), ticket);
    }

    public Ticket findTicket(String ticketId) {
        return tickets.get(ticketId);
    }

    public Map<String, Ticket> getAllTickets() {
        return tickets;
    }
}
