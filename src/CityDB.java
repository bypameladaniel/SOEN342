import java.util.HashSet;
import java.util.Set;

public class CityDB {
    private Set<City> cities = new HashSet<>();

    public void addCity(City city) {
        cities.add(city);
    }

    public City findCity(String name) {
        for (City c : cities) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
