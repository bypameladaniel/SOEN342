import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Connection {

    private String routeId;

    private City departureCity;
    private City arrivalCity;
    private String departureTime;
    private String arrivalTime;
    private String trainType;
    private String daysOfOperationFormatted;
    private Set<DayOfWeek> daysOfOperation;
    private double firstClassRate;
    private double secondClassRate;

    private static final Map<String, DayOfWeek> DAY_MAP = Map.of(
            "MON", DayOfWeek.MONDAY,
            "TUE", DayOfWeek.TUESDAY,
            "WED", DayOfWeek.WEDNESDAY,
            "THU", DayOfWeek.THURSDAY,
            "FRI", DayOfWeek.FRIDAY,
            "SAT", DayOfWeek.SATURDAY,
            "SUN", DayOfWeek.SUNDAY);

    public Connection(String routeId, City departureCity, City arrivalCity,
            String departureTime, String arrivalTime,
            String trainType, String daysOfOperationFormatted,
            double firstClassRate, double secondClassRate) {
        this.routeId = routeId;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.trainType = trainType;
        this.daysOfOperationFormatted = daysOfOperationFormatted;
        this.daysOfOperation = parseDaysOfOperation(daysOfOperationFormatted);
        this.firstClassRate = firstClassRate;
        this.secondClassRate = secondClassRate;
    }

    public Set<DayOfWeek> parseDaysOfOperation(String input) {
        Set<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);

        if (input == null || input.isBlank()) {
            return result;
        }

        input = input.trim().replace("\\s+", "");
        input = input.toUpperCase();

        if (input.equals("DAILY")) {
            return EnumSet.allOf(DayOfWeek.class);
        }

        if (input.contains(",")) {
            String[] parts = input.split(",");
            for (String part : parts) {
                DayOfWeek day = mapToDay(part);

                if (day != null) {
                    result.add(day);
                }
            }
            return result;
        }

        if (input.contains("-")) {
            String[] parts = input.split("-");
            if (parts.length == 2) {
                DayOfWeek start = mapToDay(parts[0]);
                DayOfWeek end = mapToDay(parts[1]);
                if (start != null && end != null) {
                    DayOfWeek current = start;
                    while (true) {
                        result.add(current);
                        if (current == end)
                            break;
                        current = current.plus(1);
                    }
                }
            }
            return result;
        }

        DayOfWeek singleDay = mapToDay(input);
        if (singleDay != null) {
            result.add(singleDay);
        }

        return result;
    }

    private static DayOfWeek mapToDay(String input) {
        return DAY_MAP.get(input.substring(0, 3).toUpperCase());
    }

    public int getDuration(){
        int duration = LocalTime.parse(getArrivalTime().substring(0,5)).getMinute()- LocalTime.parse(getDepartureTime()).getMinute();
        if (duration<0) {duration += 24*60;}

        return duration;
    }

    private String formatTime(int duration) {
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours + "h " + minutes + "m";
    }

    public String getRouteId() {
        return routeId;
    }

    public City getDepartureCity() {
        return departureCity;
    }

    public City getArrivalCity() {
        return arrivalCity;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getTrainType() {
        return trainType;
    }

    public String getDaysOfOperationFormatted() {
        return daysOfOperationFormatted;
    }

    public Set<DayOfWeek> getDaysOfOperation() {
        return daysOfOperation;
    }

    public double getFirstClassRate() {
        return firstClassRate;
    }

    public double getSecondClassRate() {
        return secondClassRate;
    }


    @Override
    public String toString() {
        return String.format(
                "[%s] %s → %s | %s - %s (%s)| Train: %s | Days: %s | 1st: €%.2f | 2nd: €%.2f",
                routeId,
                departureCity.getName(),
                arrivalCity.getName(),
                departureTime,
                arrivalTime,
                formatTime(getDuration()),
                trainType,
                daysOfOperationFormatted,
                firstClassRate,
                secondClassRate);
    }

}
