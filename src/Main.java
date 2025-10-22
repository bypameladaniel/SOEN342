import com.opencsv.CSVReader;
import org.apache.commons.lang3.EnumUtils;

import java.util.function.Predicate;
import java.io.FileReader;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] var0) throws Exception {
        UserInterface.printWelcome();

        ConnectionDB connectionDB = new ConnectionDB();
        CityDB cityDB = new CityDB();

        String filePath = "data/eu_rail_network.csv";
        connectionDB.loadConnections(filePath, cityDB);


        SearchQuery sq = UserInterface.printSearchParameters(cityDB);

        List<Trip> result = TripUtils.getTripAndConnections(connectionDB.getAllConnections(), sq);


        int[] sortingPreferences = UserInterface.getSortingPreferences();
        List<Trip> sortedResults = TripUtils.sortTrips(result, sortingPreferences[0], sortingPreferences[1]==1);

        UserInterface.printResult(sortedResults);


    }

//    public void tests(ConnectionDB connectionDB, CityDB cityDB){
//        /*
//         * TESTING SECTION FOR NOW
//         */
//        // Just printing to the console. To check that it's loading the DB correctly
//        for (Connection c : connectionDB.getAllConnections()) {
//            System.out.println(c);
//        }
//
//        City paris = cityDB.findCity("Amsterdam");
//        if (paris != null) {
//            for (Connection c : paris.getOutgoingConnections()) {
//                System.out.println("From Amsterdam: " + c);
//                System.out.println(c.getDaysOfOperation());
//            }
//        }
//
//        List<Trip> trips = TripUtils.findIndirectTrips(cityDB.findCity("Berh"), "Turku", cityDB);
//
//        for (Trip t : trips) {
//            System.out.println(t);
//
//        }
//
//        System.out.println(trips.size());
//
//        System.out.println("-------------SORTED TRIPS-------------");
//
//        List<Trip> sortedTrips = TripUtils.sortTrips(trips, 2, true);
//
//        for (Trip t : sortedTrips) {
//            System.out.println(t);
//        }
//
//        System.out.println(sortedTrips.size());
//
//    }

}
