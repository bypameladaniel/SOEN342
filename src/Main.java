import java.time.LocalDate;
import java.util.*;

public class Main {

    private ClientDB clientDB;
    private CityDB cityDB;
    private ConnectionDB connectionDB;
    private BookingDB bookingDB;

    public static void main(String[] var0) throws Exception {
        Main app = new Main();
        app.run();
    }

    public void run() {
        UserInterface.printWelcome();

        cityDB = new CityDB();
        connectionDB = new ConnectionDB();
        clientDB = new ClientDB();
        bookingDB = new BookingDB();

        String filePath = "data/eu_rail_network.csv";
        connectionDB.loadConnections(filePath, cityDB);

        boolean running = true;
        Scanner sc = new Scanner(System.in);

        while (running) {
            UserInterface.printMenu();
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    List<Trip> unsortedTrips = searchTrip();
                    if (unsortedTrips.isEmpty()) {
                        break;
                    }
                    List<Trip> sortedTrips = sortTrips(unsortedTrips);
                    Booking booking = selectTrip(sortedTrips);
                    if (booking == null) {
                        break;
                    }
                    List<Client> clients = enterTravelerDetails(booking);
                    bookTrip(booking, clients);
                    break;
                case "2":
                    viewClientTrips();
                    break;
                case "3":
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        sc.close();

    }

    // CO1
    public List<Trip> searchTrip() {
        SearchQuery sq = UserInterface.printSearchParameters(cityDB);

        if (sq == null)
            return null;

        List<Trip> results = connectionDB.getConnections(sq);
        if (!results.isEmpty()) {
            return results;
        } else {
            System.out.println("Could not find a single connection matching the request");
        }

        if (sq.getDepartureCity() != null && sq.getArrivalCity() != null) {
            System.out.println("Trying to find a trip with multiple connections");

            List<Trip> trips = TripUtils.findIndirectTrips(sq.getDepartureCity(), sq.getArrivalCity());
            if (trips.isEmpty()) {
                System.out.println("Could not find a trip with multiple connections");
                return results;
            } else {
                results.addAll(TripUtils.getSpecificTrip(sq, trips));
            }

        }

        return results;
    }

    // CO2
    public List<Trip> sortTrips(List<Trip> result) {

        int[] sortingPreferences = UserInterface.getSortingPreferences();
        List<Trip> sortedResults = TripUtils.sortTrips(result, sortingPreferences[0], sortingPreferences[1] == 1);

        UserInterface.printResult(sortedResults);

        return sortedResults;
    }

    // CO3
    public Booking selectTrip(List<Trip> sortedResults) {
        int tripIndex = UserInterface.getSelectedTripIndex(sortedResults);

        if (tripIndex >= 0) {
            System.out.println("Selected Trip: " + sortedResults.get(tripIndex));

        } else {
            return null;
        }

        return new Booking(sortedResults.get(tripIndex));
    }

    // CO4
    public List<Client> enterTravelerDetails(Booking booking) {

        int nbOfClients = UserInterface.getNbOfClients(booking);
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < nbOfClients; i++) {
            System.out.println("Client " + (i + 1) + ":");
            if (UserInterface.isExistingClient()) {
                ;
                Client client = UserInterface.promptClientLookup(clientDB);
                if (client != null) {
                    clients.add(client);
                    continue;
                } else {
                    System.out.println("Client not found. Please enter new client details.");
                }
            }

            Client client = UserInterface.promptClientCreation();
            clients.add(client);
        }

        return clients;
    }

    // CO5
    public void bookTrip(Booking booking, List<Client> clients) {

        LocalDate tripDate = UserInterface.promptTripDate();

        booking.setDate(tripDate);

        if (UserInterface.confirmBooking()) {

            for (Client client : clients) {
                clientDB.addClient(client);
                createReservation(client, booking);
                client.addBooking(booking);
            }

            System.out.println(booking);
        } else {
            System.out.println("Booking cancelled.");
        }
    }

    // CO6
    public void viewClientTrips() {
        Client client = UserInterface.promptClientLookup(clientDB);
        if (client != null) {
            System.out.println(client.getBookingSummary());
        }
    }

    // Could possible live in ReservationDB
    public Reservation createReservation(Client client, Booking booking) {
        Reservation reservation = new Reservation(client, booking, new Ticket());
        booking.addReservation(reservation);
        reservationDB.addReservation(reservation);
        return reservation;
    }

    // public void tests(ConnectionDB connectionDB, CityDB cityDB){
    // /*
    // * TESTING SECTION FOR NOW
    // */
    // // Just printing to the console. To check that it's loading the DB correctly
    // for (Connection c : connectionDB.getAllConnections()) {
    // System.out.println(c);
    // }
    //
    // City paris = cityDB.findCity("Amsterdam");
    // if (paris != null) {
    // for (Connection c : paris.getOutgoingConnections()) {
    // System.out.println("From Amsterdam: " + c);
    // System.out.println(c.getDaysOfOperation());
    // }
    // }
    //
    // List<Trip> trips = TripUtils.findIndirectTrips(cityDB.findCity("Berh"),
    // "Turku", cityDB);
    //
    // for (Trip t : trips) {
    // System.out.println(t);
    //
    // }
    //
    // System.out.println(trips.size());
    //
    // System.out.println("-------------SORTED TRIPS-------------");
    //
    // List<Trip> sortedTrips = TripUtils.sortTrips(trips, 2, true);
    //
    // for (Trip t : sortedTrips) {
    // System.out.println(t);
    // }
    //
    // System.out.println(sortedTrips.size());
    //
    // }

}
