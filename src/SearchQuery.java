import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SearchQuery {
    private City arrivaleCity;
    private City departureCity;
    private String arrivalTime;
    private String departureTime;
    private String trainType;
    private Set<DayOfWeek> daysOfWeek;
    private double firstClassRate;
    private double secondClassRate;

    public SearchQuery() {
        this.daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
    }

    public static City getCityInput(CityDB cityDB) {
        Scanner sc = new Scanner(System.in);
        City city;
        System.out.println("Please enter the cityStr name (respecting capitalization), else enter '*'");
        String cityStr = sc.nextLine();
        while ((city = cityDB.findCity(cityStr)) == null && !cityStr.equals("*")) {
            System.out.println("could not find cityStr: "+cityStr+". Please try again");
            cityStr = sc.nextLine();
        }
        return city;
    }

    public static String getTimeInput() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter a time in the format (hh:mm), else enter '*'");
        String time = sc.nextLine();

        while (!time.equals("*") && !time.matches( "^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {

            System.out.println("invalid time format (should be hh:mm), please try again");
            time = sc.nextLine();
        }

        return time;

    }

    public static String getNoConstrainInput() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter a parameter, else enter '*'");
        return sc.nextLine();
    }

    public void addDayInput() {
        Scanner sc = new Scanner(System.in);
        String day;

        do {
            System.out.println("Please enter a day to add, else enter '0' to quit");
            day = sc.nextLine();
            try {
                daysOfWeek.add(DayOfWeek.valueOf(day.toUpperCase()));
                System.out.println("Added "+ day+" to the list");
            } catch (IllegalArgumentException e) {
                System.out.println("invalid day");
            }

        } while (!day.equals("0"));


    }

    public void removeDayInput() {
        Scanner sc = new Scanner(System.in);
        String day;

        do {
            System.out.println("Please enter a day to remove, else enter '0' to quit");
            day = sc.nextLine();
            try {
                daysOfWeek.remove(DayOfWeek.valueOf(day.toUpperCase()));
                System.out.println("Removed "+ day+" from the list");
            } catch (IllegalArgumentException e) {
                System.out.println("invalid day");
            }

        } while (!day.equals("0"));

    }

    public static double getCurrencyInput(){
        Scanner sc = new Scanner(System.in);
        String currencyStr;
        boolean success = false;
        double currency = -1;
        while(!success){

            System.out.println("Please enter a price, else enter '*'");
            currencyStr = sc.nextLine();

            if (currencyStr.equals("*")){
                return -1;
            }

            try {
                currency = Double.parseDouble(currencyStr);
                success = true;
            } catch (NumberFormatException e) {
                System.out.println("invalid currency");
            }
        }

        return currency;

    }

    public City getArrivalCity() {
        return arrivaleCity;
    }

    public void setArrivalCity(City arrivaleCity) {
        this.arrivaleCity = arrivaleCity;
    }

    public City getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(City departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public double getFirstClassRate() {
        return firstClassRate;
    }

    public void setFirstClassRate(double firstClassRate) {
        this.firstClassRate = firstClassRate;
    }

    public double getSecondClassRate() {
        return secondClassRate;
    }

    public void setSecondClassRate(double secondClassRate) {
        this.secondClassRate = secondClassRate;
    }
}
