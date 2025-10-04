import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Trip {
    private List<Connection> connections;
    private double totalFirstClassRate;
    private double totalSecondClassRate;
    private String tripDuration;
    private int tripDurationInMinutes;
    private String waitTime;
    private int waitTimeInMinutes;

    public record NextOperationDayResult(int waitDays, DayOfWeek day) {
    }

    public Trip(List<Connection> connections) {
        this.connections = connections;
        calculateTotals();
    }

    private void calculateTotals() {
        double totalFirstClassRate = 0;
        double totalSecondClassRate = 0;
        int duration = 0;

        for (Connection c : connections) {
            totalFirstClassRate += c.getFirstClassRate();
            totalSecondClassRate += c.getSecondClassRate();
            duration += calculateDurationInMinutes(c.getDepartureTime(),
                    c.getArrivalTime());
        }
        this.totalFirstClassRate = totalFirstClassRate;
        this.totalSecondClassRate = totalSecondClassRate;

        this.waitTimeInMinutes = calculateWaitTimes();
        this.waitTime = formatWaitTime(waitTimeInMinutes);

        this.tripDurationInMinutes = duration + this.waitTimeInMinutes;
        this.tripDuration = formatDuration(this.tripDurationInMinutes);

    }

    private int parseTime(String time) {
        // Handle (+1d)
        boolean plusDay = time.contains("(+1d)");
        String clean = time.replace(" (+1d)", "").trim();

        String[] parts = clean.split(":");
        int hours = Integer.parseInt(parts[0]);
        int mins = Integer.parseInt(parts[1]);
        int total = hours * 60 + mins;

        if (plusDay) {
            total += 24 * 60; // add 1 day
        }
        return total;
    }

    private String formatDuration(int duration) {
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours + "h " + minutes + "m";
    }

    private int calculateDurationInMinutes(String dep, String arr) {
        int depMinutes = parseTime(dep);
        int arrMinutes = parseTime(arr);
        int duration = arrMinutes - depMinutes;
        if (duration < 0) {
            duration += 24 * 60; // roll into next day
        }
        return duration;
    }

    private String formatWaitTime(int waitTime) {
        int hours = waitTime / 60;
        int minutes = waitTime % 60;
        return hours + "h " + minutes + "m";
    }

    private int calculateWaitTimes() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();
        DayOfWeek nextDepartureDay = currentDay;

        if (!connections.get(0).getDaysOfOperation().contains(currentDay)) {
            NextOperationDayResult departure = getNextOperatingDay(currentDay, connections.get(0).getDaysOfOperation());
            nextDepartureDay = departure.day();
        }

        int totalWait = 0;
        for (int i = 0; i < connections.size() - 1; i++) {
            int arrMins = parseTime(connections.get(i).getArrivalTime());
            int depMins = parseTime(connections.get(i + 1).getDepartureTime());
            int wait = depMins - arrMins;

            if (!connections.get(i + 1).getDaysOfOperation().contains(nextDepartureDay) || wait < 0) {
                NextOperationDayResult nextDeparture = getNextOperatingDay(nextDepartureDay,
                        connections.get(i + 1).getDaysOfOperation());
                wait += 24 * 60 * nextDeparture.waitDays();
                nextDepartureDay = nextDeparture.day();
            }

            totalWait += wait;
        }

        return totalWait;
    }

    public static NextOperationDayResult getNextOperatingDay(DayOfWeek startDay, Set<DayOfWeek> daysOfOperation) {
        int minWaitDays = Integer.MAX_VALUE;
        DayOfWeek nextDay = null;

        for (DayOfWeek operatingDay : daysOfOperation) {
            int diff = (operatingDay.getValue() - startDay.getValue() + 7) % 7;
            if (diff == 0) {
                diff = 7;
            }
            if (diff < minWaitDays) {
                minWaitDays = diff;
                nextDay = operatingDay;
            }
        }

        return new NextOperationDayResult(minWaitDays, nextDay);
    }

    // --- Getters ---
    public List<Connection> getConnections() {
        return connections;
    }

    public double getTotalFirstClassRate() {
        return totalFirstClassRate;
    }

    public double getTotalSecondClassRate() {
        return totalSecondClassRate;
    }

    public String getTripDuration() {
        return tripDuration;
    }

    public int getTripDurationInMinutes() {
        return tripDurationInMinutes;
    }

    public String getWaitTimes() {
        return waitTime;
    }

    public int getWaitTimeInMinutes() {
        return waitTimeInMinutes;
    }

    @Override
    public String toString() {
        StringBuilder connectionsDetails = new StringBuilder();
        for (Connection connection : connections) {
            connectionsDetails.append(connection.toString()).append("\n");
        }

        return "Trip: " + connections.size() + " connections, " +
                "Duration=" + tripDuration +
                ", Wait=" + waitTime +
                ", 1st=€" + String.format("%.2f", totalFirstClassRate) +
                ", 2nd=€" + String.format("%.2f", totalSecondClassRate) +
                "\nConnections:\n" + connectionsDetails.toString();
    }
}
