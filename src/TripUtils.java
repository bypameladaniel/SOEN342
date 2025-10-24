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
