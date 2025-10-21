public class Reservation {
    private Client client;
    private Booking booking;
    private Ticket ticket;

    public Reservation(Client client, Booking booking, Ticket ticket) {
        this.client = client;
        this.booking = booking;
        this.ticket = ticket;
    }

    public Client getClient() {
        return client;
    }

    public Booking getBooking() {
        return booking;
    }

    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public String toString() {
        return String.format("Reservation for %s | Ticket: %s", 
                             client.getName(), ticket.getTicketID());
    }
}
