import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;

public class TripUtils {

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

                if (secondCity.equals(arrivalCity)) {
                    results.add(new Trip(Arrays.asList(firstLeg, secondLeg)));
                }

                // 2-stop paths
                for (Connection thirdLeg : secondCity.getOutgoingConnections()) {
                    if (thirdLeg.getArrivalCity().equals(arrivalCity)) {
                        results.add(new Trip(Arrays.asList(firstLeg, secondLeg, thirdLeg)));
                    }
                }
            }
        }

        return results;
    }

    public static List<Trip> sortTrips(List<Trip> list, int sortBy, boolean isAscending) {
        if (!list.isEmpty() && list.get(0) instanceof Trip) {
            @SuppressWarnings("unchecked")
            List<Trip> typedList = (List<Trip>) list;
            List<Trip> sortedTrips = new ArrayList<>(typedList);
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

    public static List<Trip> getTripAndConnections(List<Connection> connections, SearchQuery searchQuery) {
        List<Trip> results = getSpecitficConnections(searchQuery, connections);
        if (!results.isEmpty()) {
            return results;
        } else {
            System.out.println("Could not find a single connection matching the request");
        }

        if (searchQuery.getDepartureCity() != null && searchQuery.getArrivalCity() != null) {
            System.out.println("Trying to find a trip with multiple connections");

            List<Trip> trips = findIndirectTrips(searchQuery.getDepartureCity(), searchQuery.getArrivalCity());
            if (trips.isEmpty()) {
                System.out.println("Could not find a trip with multiple connections");
                return results;
            } else {
                results.addAll(getSpecificTrip(searchQuery, trips));
            }

        }

        return results;

    }

    public static List<Trip> getSpecitficConnections(SearchQuery searchQuery, List<Connection> connections) {
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
