import java.util.ArrayList;
import java.util.List;

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
}
