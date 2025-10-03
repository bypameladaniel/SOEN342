import java.util.List;

public class Trip {
    private List<Connection> connections;
    private double totalFirstClassRate;
    private double totalSecondClassRate;
    private String tripDuration;
    private int tripDurationInMinutes;
    private String waitTimes;

    public Trip(List<Connection> connections) {
        this.connections = connections;
        calculateTotals();
    }

    // Calculates wrongly, need to change later.
    private void calculateTotals() {
        double first = 0;
        double second = 0;

        for (Connection c : connections) {
            first += c.getFirstClassRate();
            second += c.getSecondClassRate();
        }
        this.totalFirstClassRate = first;
        this.totalSecondClassRate = second;

        // basic trip duration = last arrival - first departure
        Connection firstConn = connections.get(0);
        Connection lastConn = connections.get(connections.size() - 1);
        this.tripDurationInMinutes = calculateDurationInMinutes(firstConn.getDepartureTime(),
                lastConn.getArrivalTime());
        this.tripDuration = formatDuration(this.tripDurationInMinutes);

        // wait times
        this.waitTimes = calculateWaitTimes();
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

    public int calculateDurationInMinutes(String dep, String arr) {
        int depMinutes = parseTime(dep);
        int arrMinutes = parseTime(arr);
        int duration = arrMinutes - depMinutes;
        if (duration < 0) {
            duration += 24 * 60; // roll into next day
        }
        return duration;
    }

    private String calculateWaitTimes() {
        int totalWait = 0;
        for (int i = 0; i < connections.size() - 1; i++) {
            int arrMins = parseTime(connections.get(i).getArrivalTime());
            int depMins = parseTime(connections.get(i + 1).getDepartureTime());
            int wait = depMins - arrMins;
            if (wait < 0) {
                wait += 24 * 60; // next day departure
            }
            totalWait += wait;
        }
        int hours = totalWait / 60;
        int mins = totalWait % 60;
        return hours + "h " + mins + "m";
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
        return waitTimes;
    }

    @Override
    public String toString() {
        StringBuilder connectionsDetails = new StringBuilder();
        for (Connection connection : connections) {
            connectionsDetails.append(connection.toString()).append("\n");
        }

        return "Trip: " + connections.size() + " connections, " +
                "Duration=" + tripDuration +
                ", Wait=" + waitTimes +
                ", 1st=€" + String.format("%.2f", totalFirstClassRate) +
                ", 2nd=€" + String.format("%.2f", totalSecondClassRate);
        // "\nConnections:\n" + connectionsDetails.toString();
    }
}
