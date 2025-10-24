import com.opencsv.CSVReader;

import java.io.FileReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ConnectionDB {
    private List<Connection> connections;

    public ConnectionDB() {
        this.connections = new ArrayList<>();
    }

    public void addConnection(Connection conn) {
        connections.add(conn);
    }

    public List<Connection> getAllConnections() {
        return connections;
    }

    public List<Trip> getConnections(SearchQuery searchQuery) {
        List<Trip> results = new ArrayList<>();
        Predicate<Connection> generalPredicate = connection -> connections != null;

        if (searchQuery.getDepartureCity() != null) {
            Predicate<Connection> departureCityPredicate = connection -> connection.getDepartureCity()
                    .equals(searchQuery.getDepartureCity());
            generalPredicate = generalPredicate.and(departureCityPredicate);
        }
        if (searchQuery.getArrivalCity() != null) {
            Predicate<Connection> arrivalCityPredicate = connection -> connection.getArrivalCity()
                    .equals(searchQuery.getArrivalCity());
            generalPredicate = generalPredicate.and(arrivalCityPredicate);
        }

        // departure and arrival predicate
        if (searchQuery.getDepartureTime() != null) {
            LocalTime departureTimeStart = LocalTime.parse(searchQuery.getDepartureTime());
            LocalTime departureTimeEnd = departureTimeStart.plusHours(1);

            Predicate<Connection> departureTimePredicate = c -> LocalTime.parse(c.getDepartureTime())
                    .isAfter(departureTimeStart) &&
                    LocalTime.parse(c.getDepartureTime()).isBefore(departureTimeEnd);
            generalPredicate = generalPredicate.and(departureTimePredicate);
        }

        if (searchQuery.getArrivalTime() != null) {
            LocalTime arrivalTimeEnd = LocalTime.parse(searchQuery.getArrivalTime());
            LocalTime arrivalTimeStart = arrivalTimeEnd.minusHours(1);

            Predicate<Connection> arrivalTimePredicate = c -> LocalTime.parse(c.getArrivalTime().substring(0, 5))
                    .isAfter(arrivalTimeStart) &&
                    LocalTime.parse(c.getArrivalTime().substring(0, 5)).isBefore(arrivalTimeEnd);

            generalPredicate = generalPredicate.and(arrivalTimePredicate);
        }

        if (searchQuery.getTrainType() != null) {
            Predicate<Connection> trainTypePredicate = connection -> connection.getTrainType()
                    .equals(searchQuery.getTrainType());
            generalPredicate = generalPredicate.and(trainTypePredicate);
        }

        if (searchQuery.getDaysOfWeek() != null) {
            Predicate<Connection> daysOfWeekPredicate = connection -> !Collections
                    .disjoint(connection.getDaysOfOperation(), searchQuery.getDaysOfWeek());
            generalPredicate = generalPredicate.and(daysOfWeekPredicate);
        }

        if (searchQuery.getFirstClassRate() != 0.0) {
            Predicate<Connection> firstClassRatePredicate = connection -> searchQuery.getFirstClassRate() == connection
                    .getFirstClassRate();
            generalPredicate = generalPredicate.and(firstClassRatePredicate);
        }
        if (searchQuery.getSecondClassRate() != 0.0) {
            Predicate<Connection> secondClassRatePredicate = connection -> searchQuery
                    .getSecondClassRate() == connection.getSecondClassRate();
            generalPredicate = generalPredicate.and(secondClassRatePredicate);
        }

        // executing the queries
        for (Connection connection : connections) {
            if (generalPredicate.test(connection)) {
                results.add(new Trip(List.of(connection)));
            }
        }

        return results;
    }

    public void loadConnections(String filePath, CityDB cityDB) {
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
                connections.add(conn);
                departureCity.addOutgoingConnection(conn);
                arrivalCity.addIncomingConnection(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
