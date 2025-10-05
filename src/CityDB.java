import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CityDB {
    private Map<String, City> cities = new HashMap<>();
    
    public void addCity(City city) {
        cities.put(city.getName(), city); // store with same capitalization
    }
    
    public City findCity(String name) {
        return cities.get(name); // must match capitalization (e.g., "Paris")
    }

}
