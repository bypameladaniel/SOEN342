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

    public Client finClientByIDAndName(Long clientID, String name) {
        Client client = clients.get(clientID);
        if (client != null && client.getLastName() != null && client.getLastName().equalsIgnoreCase(name)) {
            return client;
        }
        return null;
    }

    public Client findClientByLastName(String name) {
        for (Client c : clients.values()) {
            if (c.getLastName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }
}
