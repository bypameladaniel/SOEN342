import java.util.HashMap;
import java.util.Map;

public class ClientDB {
    private Map<String, Client> clients = new HashMap<>();

    public void addClient(Client client) {
        clients.put(client.getClientID(), client);
    }

    public Client findClientById(String clientID) {
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

    public Map<String, Client> getAllClients() {
        return clients;
    }
}
