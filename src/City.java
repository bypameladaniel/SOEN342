import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class City {
    private String name;
    private List<Connection> outgoingConnections;
    private List<Connection> incomingConnections;

    public City(String name) {
        this.name = name;
        this.outgoingConnections = new ArrayList<>();
        this.incomingConnections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Connection> getOutgoingConnections() {
        return outgoingConnections;
    }

    public List<Connection> getIncomingConnections() {
        return incomingConnections;
    }

    public void addOutgoingConnection(Connection conn) {
        outgoingConnections.add(conn);
    }

    public void addIncomingConnection(Connection conn) {
        incomingConnections.add(conn);
    }

    // Needed so HashSet can recognize cities with the same name as duplicates
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return name.equalsIgnoreCase(city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
