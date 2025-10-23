import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientDB {
    private Map<Long, Client> clients = new HashMap<>();

    public void addClient(Client client) {
        clients.put(client.getClientID(), client);
    }

    public Client findClientById(Long clientID) {
        return clients.get(clientID);
    }

    public Client findClientByName(String name) {
        for (Client c : clients.values()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }
}
