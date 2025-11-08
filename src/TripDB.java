import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TripDB {
    private java.sql.Connection dbConnection;
    private HashMap<Long,Trip> trips;

    public TripDB(ConnectionDB connectionDB) {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:soen342.db");
            createTableIfNotExists();
            getFromTripDB(connectionDB);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Trip (
                tripId TEXT PRIMARY KEY,
                totalFirstClassRate REAL,
                totalSecondClassRate REAL,
                tripDuration TEXT,
                tripDurationInMinutes INTEGER,
                waitTime TEXT,
                waitTimeInMinutes INTEGER
            );
        """;

        String sql2 = """
            CREATE TABLE IF NOT EXISTS TripConnections (
                tripId INTEGER ,
                connectionId TEXT,
                PRIMARY KEY (tripId, connectionId),
                FOREIGN KEY (tripId) REFERENCES Trip(tripId),
                FOREIGN KEY (connectionId) REFERENCES Connection(routeId)
            );
        """;

        try {
            dbConnection.createStatement().execute(sql);
            dbConnection.createStatement().execute(sql2);
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }

    private void addTripConnection(Long tripId, String connectionId) {
        String sql = "INSERT INTO TripConnections (tripId, connectionId) VALUES (?, ?)";
        try (var stmt = dbConnection.prepareStatement(sql)) {
            stmt.setLong(1, tripId);
            stmt.setString(2, connectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFromTripConnectionsDB(String tripId) {
        List<String> connections = new ArrayList<>();
        String query = "SELECT connectionId FROM TripConnections WHERE tripId = ?";
        try (var stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, tripId);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    connections.add(rs.getString("connectionId")) ;

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connections;
    }

    public void getFromTripDB(ConnectionDB connectionDB) {
        String query = "SELECT * FROM Trip";
        try (var stmt = dbConnection.createStatement();
             var rs = stmt.executeQuery(query)) {

            trips = new HashMap<>();
            while (rs.next()) {
                Trip trip = new Trip(rs.getLong(1),
                        rs.getDouble(2), rs.getDouble(3),
                        rs.getString(4), rs.getInt(5),
                        rs.getString(6), rs.getInt(7));

                trip.setConnectionsIds(getFromTripConnectionsDB(rs.getString(1)));

                trip.setConnections(connectionDB.getConnectionsByIds(trip.getConnectionsIds()));


                trips.put(trip.getTripId(), trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Long addTrip(Trip trip) {
        String sql = "INSERT INTO Trip (tripId, totalFirstClassRate, totalSecondClassRate, " +
                "tripDuration, tripDurationInMinutes, waitTime, waitTimeInMinutes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var stmt = dbConnection.prepareStatement(sql)) {
            stmt.setLong(1, trip.getTripId());
            stmt.setDouble(2, trip.getTotalFirstClassRate());
            stmt.setDouble(3, trip.getTotalSecondClassRate());
            stmt.setString(4, trip.getTripDuration());
            stmt.setInt(5, trip.getTripDurationInMinutes());
            stmt.setString(6, trip.getWaitTime());
            stmt.setInt(7, trip.getWaitTimeInMinutes());
            stmt.executeUpdate();

            for (Connection c : trip.getConnections()) {
                addTripConnection(trip.getTripId(), c.getRouteId());
            }

            trips.put(trip.getTripId(), trip);
            return trip.getTripId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public Trip findTripById(Long tripId) {
        return trips.get(tripId);
    }

    private static boolean searchTripPolicy(Connection firstConnection, Connection secondConnection) {
        boolean layoverAccepted;

        LocalTime firstArrivalTime = LocalTime.parse(firstConnection.getArrivalTime().substring(0, 5));
        LocalTime secondDepartureTime = LocalTime.parse(secondConnection.getDepartureTime().substring(0, 5));

        LocalTime afterHoursStart = LocalTime.of(22, 0);
        LocalTime afterHoursEnd = LocalTime.of(5, 0);

        boolean isAfterHours = firstArrivalTime.isAfter(afterHoursStart) || firstArrivalTime.isBefore(afterHoursEnd);

        int arrivalMinutes = firstArrivalTime.getHour() * 60 + firstArrivalTime.getMinute();
        int departureMinutes = secondDepartureTime.getHour() * 60 + secondDepartureTime.getMinute();
        int duration = departureMinutes - arrivalMinutes;

        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();
        NextOperationDayResult departureDay = Trip.getNextOperatingDay(currentDay,
                firstConnection.getDaysOfOperation());

        if (!secondConnection.getDaysOfOperation().contains(departureDay.day()) || duration < 0) {
            NextOperationDayResult layover = Trip.getNextOperatingDay(departureDay.day(),
                    secondConnection.getDaysOfOperation());
            layoverAccepted = layover.waitDays() == 1 && duration < 0;
        } else {
            layoverAccepted = true;
        }

        if (layoverAccepted) {
            if (duration < 0) {
                duration += 24 * 60;
            }
            if (isAfterHours) {
                return duration < 120;
            } else {
                return duration < 240;
            }
        }
        return false;
    }

    public static List<Trip> findIndirectTrips(City departureCity, City arrivalCity) {
        List<Trip> results = new ArrayList<>();

        if (departureCity == null || arrivalCity == null) {
            return results; // no such city
        }

        // Explore all 1-stop and 2-stop paths
        for (Connection firstLeg : departureCity.getOutgoingConnections()) {
            City midCity = firstLeg.getArrivalCity();

            // Direct path (1 leg = already a trip)
            if (midCity.equals(arrivalCity)) {
                results.add(new Trip(Arrays.asList(firstLeg)));
            }

            // 1-stop paths
            for (Connection secondLeg : midCity.getOutgoingConnections()) {
                City secondCity = secondLeg.getArrivalCity();

                if (secondCity.equals(arrivalCity) && searchTripPolicy(firstLeg, secondLeg)) {
                    results.add(new Trip(Arrays.asList(firstLeg, secondLeg)));
                }

                // 2-stop paths
                for (Connection thirdLeg : secondCity.getOutgoingConnections()) {
                    if (thirdLeg.getArrivalCity().equals(arrivalCity) && searchTripPolicy(firstLeg, secondLeg)
                            && searchTripPolicy(secondLeg, thirdLeg)) {
                        results.add(new Trip(Arrays.asList(firstLeg, secondLeg, thirdLeg)));
                    }
                }
            }
        }

        return results;
    }

    public static List<Trip> sortTrips(List<Trip> list, int sortBy, boolean isAscending) {
        if (!list.isEmpty()) {
            List<Trip> sortedTrips = new ArrayList<>(list);
            sortedTrips.sort((Comparator<Trip>) getTripComparator(sortBy, isAscending));
            return sortedTrips;
        }
        return list;
    }

    public static Comparator<Trip> getTripComparator(int sortBy, boolean isAscending) {
        Comparator<Trip> comparator = switch (sortBy) {
            case 1 -> Comparator.comparingDouble(trip -> trip.getTotalFirstClassRate());
            case 2 -> Comparator.comparingDouble(trip -> trip.getTotalSecondClassRate());
            case 3 -> Comparator.comparingInt(trip -> trip.getTripDurationInMinutes());
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };
        return isAscending ? comparator : comparator.reversed();
    }

    public static List<Trip> getSpecificTrip(SearchQuery searchQuery, List<Trip> trips) {
        List<Trip> results = new ArrayList<>(trips);

        LocalTime departureTimeStart = LocalTime.of(0, 0);
        LocalTime departureTimeEnd = LocalTime.of(0, 0);

        if (searchQuery.getDepartureTime() != null) {
            departureTimeStart = LocalTime.parse(searchQuery.getDepartureTime());
            departureTimeEnd = departureTimeStart.plusHours(1);
        }

        LocalTime arrivalTimeEnd = LocalTime.of(0, 0);
        LocalTime arrivalTimeStart = LocalTime.of(0, 0);

        if (searchQuery.getArrivalTime() != null) {
            arrivalTimeEnd = LocalTime.parse(searchQuery.getArrivalTime().substring(0, 5));
            arrivalTimeStart = arrivalTimeEnd.minusHours(1);
        }

        for (Trip trip : trips) {
            if (searchQuery.getDepartureTime() != null) {
                LocalTime tripDeparture = LocalTime.parse(trip.getConnections().get(0).getDepartureTime());
                if (!(tripDeparture.isAfter(arrivalTimeStart) &&
                        tripDeparture.isBefore(arrivalTimeEnd))) {
                    results.remove(trip);
                }
            }

            if (searchQuery.getDepartureTime() != null) {
                LocalTime tripDeparture = LocalTime.parse(
                        trip.getConnections().get(trip.getConnections().size() - 1).getArrivalTime().substring(0, 5));
                if (!(tripDeparture.isAfter(arrivalTimeStart) &&
                        tripDeparture.isBefore(arrivalTimeEnd))) {
                    results.remove(trip);
                }
            }

            if (searchQuery.getTrainType() != null) {
                for (Connection connection : trip.getConnections()) {
                    if (!searchQuery.getTrainType().equals(connection.getTrainType())) {
                        results.remove(trip);
                    }
                }
            }

            if (searchQuery.getDaysOfWeek() != null &&
                    Collections.disjoint(
                            trip.getConnections().get(0).getDaysOfOperation(),
                            searchQuery.getDaysOfWeek())) {
                results.remove(trip);
            }

            if (searchQuery.getFirstClassRate() != 0.0
                    && trip.getTotalFirstClassRate() != searchQuery.getFirstClassRate()) {
                results.remove(trip);
            }

            if (searchQuery.getSecondClassRate() != 0.0
                    && trip.getTotalSecondClassRate() != searchQuery.getSecondClassRate()) {
                results.remove(trip);
            }

        }

        return results;
    }

}

