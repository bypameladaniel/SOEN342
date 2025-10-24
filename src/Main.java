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

        connectionDB = new ConnectionDB();
        cityDB = new CityDB();
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
                    searchAndBookTrip();
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

    public void searchAndBookTrip() {
        SearchQuery sq = UserInterface.printSearchParameters(cityDB);

        if (sq == null)
            return;

        List<Trip> result = TripUtils.getTripAndConnections(connectionDB.getAllConnections(), sq);
        
        int[] sortingPreferences = UserInterface.getSortingPreferences();
        List<Trip> sortedResults = TripUtils.sortTrips(result, sortingPreferences[0], sortingPreferences[1] == 1);

        UserInterface.printResult(sortedResults);

        int tripIndex = UserInterface.getSelectedTripIndex(sortedResults);

        // Start booking
        if (tripIndex != -1) {
            System.out.println("Selected Trip: " + sortedResults.get(tripIndex));

            Booking booking = new Booking(sortedResults.get(tripIndex));

            int nbOfClients = UserInterface.getNbOfClients(booking);
            List<Client> clients = new ArrayList<>();

            for (int i = 0; i < nbOfClients; i++) {
                System.out.println("Client " + (i + 1) + ":");
                String firstName = UserInterface.promptClientFirstName();
                String lastName = UserInterface.promptClientLastName();
                int age = UserInterface.promptClientAge();
                Long id = UserInterface.promptClientId();
                Client client = enterTravelerDetails(firstName, lastName, age, id);
                clients.add(client);
                createReservation(client, booking);
            }

            LocalDate tripDate = UserInterface.promptTripDate();

            if (UserInterface.confirmBooking()) {
                bookTrip(booking, clients, tripDate);
                System.out.println(booking);
            } else {
                System.out.println("Booking cancelled.");
            }

        }
    }

    public Client enterTravelerDetails(String firstName, String lastName, int age, Long id) {
        Client client;
        client = clientDB.findClientById(id);
        if (client == null) {
            client = new Client(id, firstName, lastName, age);
        }
        return client;
    }

    // Could possible live in ReservationDB
    public Reservation createReservation(Client client, Booking booking) {
        Reservation reservation = new Reservation(client, booking, new Ticket());
        booking.addReservation(reservation);
        return reservation;
    }

    public void bookTrip(Booking booking, List<Client> clients, LocalDate date) {
        for (Client client : clients) {
            client.addBooking(booking);
            clientDB.addClient(client);
        }
        booking.setDate(date);
        bookingDB.addBooking(booking);

    }

    public void viewClientTrips() {
        Client client = UserInterface.promptClientLookup(clientDB);
        if (client != null) {
            System.out.println(client.getBookingSummary());
        }
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
