import com.opencsv.CSVReader;

import java.io.FileReader;
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

    public void loadConnections(String filePath, CityDB cityDB) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] row;
            reader.readNext(); // skip header row

            while ((row = reader.readNext()) != null) {
                City departureCity = cityDB.findCity(row[1]);
                if (departureCity == null) {
                    departureCity = new City(row[1]);
                    cityDB.addCity(departureCity);
                }

                City arrivalCity = cityDB.findCity(row[2]);
                if (arrivalCity == null) {
                    arrivalCity = new City(row[2]);
                    cityDB.addCity(arrivalCity);
                }

                Connection conn = new Connection(
                        row[0], // routeId
                        departureCity, // departureCity name
                        arrivalCity, // arrivalCity name
                        row[3], // departureTime
                        row[4], // arrivalTime
                        row[5], // trainType
                        row[6], // daysOfOperation
                        Double.parseDouble(row[7]), // firstClassRate
                        Double.parseDouble(row[8]) // secondClassRate
                );

                // --- Add to DB and link cities ---
                connections.add(conn);
                departureCity.addOutgoingConnection(conn);
                arrivalCity.addIncomingConnection(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
