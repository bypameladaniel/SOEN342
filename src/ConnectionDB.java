import com.opencsv.CSVReader;

import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.sql.*;


public class ConnectionDB {
    private List<Connection> connections;
    
    private java.sql.Connection dbConnection;


    public ConnectionDB(CityDB cityDB) {
        this.connections = new ArrayList<>();
        
        try {
            dbConnection = java.sql.DriverManager.getConnection("jdbc:sqlite:soen342.db");

            createTablesIfNotExist();
            getConnectionFromDB(cityDB);
            System.out.println("✅ SQLite connected in ConnectionDB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Could not connect to database.");
            e.printStackTrace();
        }
    }

    public void addConnection(Connection conn) {
        connections.add(conn);
    }

    public List<Connection> getAllConnections() {
        return connections;
    }

    private void createTablesIfNotExist() throws SQLException {
        // City table moved to CityDB to centralize City schema management.

        String connectionTable = """
            CREATE TABLE IF NOT EXISTS Connection (
                routeId TEXT PRIMARY KEY,
                departureCity TEXT,
                arrivalCity TEXT,
                departureTime TEXT,
                arrivalTime TEXT,
                trainType TEXT,
                daysOfOperation TEXT,
                firstClassRate REAL,
                secondClassRate REAL,
                FOREIGN KEY (departureCity) REFERENCES City(name),
                FOREIGN KEY (arrivalCity) REFERENCES City(name)
            );
        """;

        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute(connectionTable);
        }
    }
    

    public List<Trip> getConnections(SearchQuery searchQuery) {
        List<Trip> results = new ArrayList<>();
        Predicate<Connection> generalPredicate = connection -> connections != null;

        if (searchQuery.getDepartureCity() != null) {
            Predicate<Connection> departureCityPredicate = connection -> connection.getDepartureCity()
                    .equals(searchQuery.getDepartureCity());
            generalPredicate = generalPredicate.and(departureCityPredicate);
        }
        if (searchQuery.getArrivalCity() != null) {
            Predicate<Connection> arrivalCityPredicate = connection -> connection.getArrivalCity()
                    .equals(searchQuery.getArrivalCity());
            generalPredicate = generalPredicate.and(arrivalCityPredicate);
        }

        // departure and arrival predicate
        if (searchQuery.getDepartureTime() != null) {
            LocalTime departureTimeStart = LocalTime.parse(searchQuery.getDepartureTime());
            LocalTime departureTimeEnd = departureTimeStart.plusHours(1);

            Predicate<Connection> departureTimePredicate = c -> LocalTime.parse(c.getDepartureTime())
                    .isAfter(departureTimeStart) &&
                    LocalTime.parse(c.getDepartureTime()).isBefore(departureTimeEnd);
            generalPredicate = generalPredicate.and(departureTimePredicate);
        }

        if (searchQuery.getArrivalTime() != null) {
            LocalTime arrivalTimeEnd = LocalTime.parse(searchQuery.getArrivalTime());
            LocalTime arrivalTimeStart = arrivalTimeEnd.minusHours(1);

            Predicate<Connection> arrivalTimePredicate = c -> LocalTime.parse(c.getArrivalTime().substring(0, 5))
                    .isAfter(arrivalTimeStart) &&
                    LocalTime.parse(c.getArrivalTime().substring(0, 5)).isBefore(arrivalTimeEnd);

            generalPredicate = generalPredicate.and(arrivalTimePredicate);
        }

        if (searchQuery.getTrainType() != null) {
            Predicate<Connection> trainTypePredicate = connection -> connection.getTrainType()
                    .equals(searchQuery.getTrainType());
            generalPredicate = generalPredicate.and(trainTypePredicate);
        }

        if (searchQuery.getDaysOfWeek() != null) {
            Predicate<Connection> daysOfWeekPredicate = connection -> !Collections
                    .disjoint(connection.getDaysOfOperation(), searchQuery.getDaysOfWeek());
            generalPredicate = generalPredicate.and(daysOfWeekPredicate);
        }

        if (searchQuery.getFirstClassRate() != 0.0) {
            Predicate<Connection> firstClassRatePredicate = connection -> searchQuery.getFirstClassRate() == connection
                    .getFirstClassRate();
            generalPredicate = generalPredicate.and(firstClassRatePredicate);
        }
        if (searchQuery.getSecondClassRate() != 0.0) {
            Predicate<Connection> secondClassRatePredicate = connection -> searchQuery
                    .getSecondClassRate() == connection.getSecondClassRate();
            generalPredicate = generalPredicate.and(secondClassRatePredicate);
        }

        // executing the queries
        for (Connection connection : connections) {
            if (generalPredicate.test(connection)) {
                results.add(new Trip(List.of(connection)));
            }
        }

        return results;
    }

    public void loadConnections(String filePath, CityDB cityDB) {
    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
        String[] row;
        reader.readNext(); // skip header

        PreparedStatement insertCity = dbConnection.prepareStatement(
            "INSERT OR IGNORE INTO City (name) VALUES (?)"
        );

        PreparedStatement insertConn = dbConnection.prepareStatement(
            "INSERT OR REPLACE INTO Connection (routeId, departureCity, arrivalCity, departureTime, arrivalTime, trainType, daysOfOperation, firstClassRate, secondClassRate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

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
                    row[0], departureCity, arrivalCity,
                    row[3], row[4], row[5], row[6],
                    Double.parseDouble(row[7]), Double.parseDouble(row[8])
            );

            departureCity.addOutgoingConnection(conn);
            arrivalCity.addIncomingConnection(conn);

            // --- new persistence part ---
            insertCity.setString(1, departureCity.getName());
            insertCity.executeUpdate();

            insertCity.setString(1, arrivalCity.getName());
            insertCity.executeUpdate();

            insertConn.setString(1, row[0]);
            insertConn.setString(2, departureCity.getName());
            insertConn.setString(3, arrivalCity.getName());
            insertConn.setString(4, row[3]);
            insertConn.setString(5, row[4]);
            insertConn.setString(6, row[5]);
            insertConn.setString(7, row[6]);
            insertConn.setDouble(8, Double.parseDouble(row[7]));
            insertConn.setDouble(9, Double.parseDouble(row[8]));
            insertConn.executeUpdate();
        }

        insertCity.close();
        insertConn.close();

        System.out.println("✅ Connections persisted successfully.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void getConnectionFromDB(CityDB cityDB) {
        String query = "SELECT routeId, departureCity, arrivalCity, departureTime, arrivalTime, trainType, daysOfOperation, firstClassRate, secondClassRate FROM Connection";
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // clear existing to avoid duplicates
            connections.clear();

            while (rs.next()) {
                String routeId = rs.getString("routeId");
                String depName = rs.getString("departureCity");
                String arrName = rs.getString("arrivalCity");
                String depTime = rs.getString("departureTime");
                String arrTime = rs.getString("arrivalTime");
                String trainType = rs.getString("trainType");
                String days = rs.getString("daysOfOperation");
                double firstRate = rs.getDouble("firstClassRate");
                double secondRate = rs.getDouble("secondClassRate");

                City departureCity = cityDB.findCity(depName);
                if (departureCity == null) {
                    departureCity = new City(depName);
                    cityDB.addCity(departureCity);
                }

                City arrivalCity = cityDB.findCity(arrName);
                if (arrivalCity == null) {
                    arrivalCity = new City(arrName);
                    cityDB.addCity(arrivalCity);
                }

                Connection conn = new Connection(
                        routeId, departureCity, arrivalCity,
                        depTime, arrTime, trainType, days,
                        firstRate, secondRate
                );

                connections.add(conn);
                departureCity.addOutgoingConnection(conn);
                arrivalCity.addIncomingConnection(conn);
            }

            System.out.println("✅ Loaded " + connections.size() + " connections from DB.");
        } catch (SQLException e) {
            System.err.println("⚠️ Failed to load connections from DB.");
            e.printStackTrace();
        }
    }

    public List<Connection> getConnectionsByIds(List<String> connectionsIds) {

        List<Connection> result = new ArrayList<>();
        for (String id : connectionsIds) {
            for (Connection conn : connections) {
                if (conn.getRouteId().equals(id)) {
                    result.add(conn);
                    break;
                }
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return connections.isEmpty();
    }
}
