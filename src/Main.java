import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] var0) throws Exception {
        ConnectionDB connectionDB = new ConnectionDB();

        CityDB cityDB = new CityDB();

        String filePath = "data/eu_rail_network.csv";

        loadConnections(filePath, connectionDB, cityDB);

        /*
         * TESTING SECTION FOR NOW
         */

        // Just printing to the console. To check that it's loading the DB correctly
        for (Connection c : connectionDB.getAllConnections()) {
            System.out.println(c);
        }

        City paris = cityDB.findCity("Amsterdam");
        if (paris != null) {
            for (Connection c : paris.getOutgoingConnections()) {
                System.out.println("From Amsterdam: " + c);
                System.out.println(c.getDaysOfOperation());
            }
        }

        List<Trip> trips = findIndirectTrips("Bergen", "Turku", cityDB);

        for (Trip t : trips) {
            System.out.println(t);

        }

        System.out.println(trips.size());

        System.out.println("-------------SORTED TRIPS-------------");

        List<Trip> sortedTrips = sortTrips(trips, 2, true);

        for (Trip t : sortedTrips) {
            System.out.println(t);
        }

        System.out.println(sortedTrips.size());

    }

    public static void loadConnections(String filePath, ConnectionDB connectionDB, CityDB cityDB) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] row;
            reader.readNext(); // skip header row

            while ((row = reader.readNext()) != null) {
                City departureCity = cityDB.findCity(row[1]);
                if (departureCity == null) {
                    departureCity = new City(row[1]);
                    cityDB.addCity(departureCity);
                }

                City arrivalCity = cityDB.findCity(row[2]);
                if (arrivalCity == null) {
                    arrivalCity = new City(row[2]);
                    cityDB.addCity(arrivalCity);
                }

                Connection conn = new Connection(
                        row[0], // routeId
                        departureCity, // departureCity name
                        arrivalCity, // arrivalCity name
                        row[3], // departureTime
                        row[4], // arrivalTime
                        row[5], // trainType
                        row[6], // daysOfOperation
                        Double.parseDouble(row[7]), // firstClassRate
                        Double.parseDouble(row[8]) // secondClassRate
                );

                // --- Add to DB and link cities ---
                connectionDB.addConnection(conn);
                departureCity.addOutgoingConnection(conn);
                arrivalCity.addIncomingConnection(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Trip> findIndirectTrips(String from, String to, CityDB cityDB) {
        List<Trip> results = new ArrayList<>();

        City startCity = cityDB.findCity(from);
        City endCity = cityDB.findCity(to);

        if (startCity == null || endCity == null) {
            return results; // no such city
        }

        // Explore all 1-stop and 2-stop paths
        for (Connection firstLeg : startCity.getOutgoingConnections()) {
            City midCity = firstLeg.getArrivalCity();

            // Direct path (1 leg = already a trip)
            if (midCity.equals(endCity)) {
                results.add(new Trip(Arrays.asList(firstLeg)));
            }

            // 1-stop paths
            for (Connection secondLeg : midCity.getOutgoingConnections()) {
                City secondCity = secondLeg.getArrivalCity();

                if (secondCity.equals(endCity)) {
                    results.add(new Trip(Arrays.asList(firstLeg, secondLeg)));
                }

                // 2-stop paths
                for (Connection thirdLeg : secondCity.getOutgoingConnections()) {
                    if (thirdLeg.getArrivalCity().equals(endCity)) {
                        results.add(new Trip(Arrays.asList(firstLeg, secondLeg, thirdLeg)));
                    }
                }
            }
        }

        return results;
    }

    // public static List<Trip> searchTrips(String from, String to, CityDB cityDB) {
    // List<Trip> results = new ArrayList<>();
    // City departure = cityDB.findCity(from);

    // if (departure == null) {
    // System.out.println("No such city: " + from);
    // return results;
    // }

    // // Direct connections
    // for (Connection c : departure.getOutgoingConnections()) {
    // if (c.getArrivalCity().equalsIgnoreCase(to)) {
    // List<Connection> conns = new ArrayList<>();
    // conns.add(c);
    // results.add(new Trip(conns));
    // }
    // }

    // return results;
    // }

    public static List<Trip> sortTrips(List<Trip> trips, int sortBy, boolean isAscending) {
        List<Trip> sortedTrips = new ArrayList<Trip>(trips);
        sortedTrips.sort(getComparator(sortBy, isAscending));
        return sortedTrips;
    }

    public static Comparator<Trip> getComparator(int sortBy, boolean isAscending) {
        Comparator<Trip> comparator = switch (sortBy) {
            case 0 -> Comparator.comparingDouble(trip -> trip.getTotalFirstClassRate());
            case 1 -> Comparator.comparingDouble(trip -> trip.getTotalSecondClassRate());
            case 2 -> Comparator.comparingInt(trip -> trip.getTripDurationInMinutes());
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };

        return isAscending ? comparator : comparator.reversed();
    }
}
