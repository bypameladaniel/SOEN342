public class Reservation {
    private int reservationID;
    private Client client;
    private Booking booking;
    private Ticket ticket;

    public Reservation(Client client, Booking booking, Ticket ticket) {
        this.client = client;
        this.booking = booking;
        this.ticket = ticket;
    }
    public void  setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public int getReservationID() {
        return reservationID;
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
        return String.format("Reservation for %s %s | Ticket: %s",
                             client.getFirstName(), client.getLastName(), ticket.getTicketID());
    }
}
