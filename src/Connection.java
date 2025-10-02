public class Connection {
    
    private String routeId; 
    
    public City departureCity;
    public City arrivalCity;
    public String departureTime;
    public String arrivalTime;
    public String trainType;
    public String daysOfOperation;
    public double firstClassRate;
    public double secondClassRate;

    public Connection(String routeId, City departureCity, City arrivalCity,
                      String departureTime, String arrivalTime,
                      String trainType, String daysOfOperation,
                      double firstClassRate, double secondClassRate) {
        this.routeId = routeId; 
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.trainType = trainType;
        this.daysOfOperation = daysOfOperation;
        this.firstClassRate = firstClassRate;
        this.secondClassRate = secondClassRate;
    }

    public String getRouteId() {
        return routeId;
    }

    public City getDepartureCity() {
        return departureCity;
    }

    public City getArrivalCity() {
        return arrivalCity;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getTrainType() {
        return trainType;
    }

    public String getDaysOfOperation() {
        return daysOfOperation;
    }

    public double getFirstClassRate() {
        return firstClassRate;
    }

    public double getSecondClassRate() {
        return secondClassRate;
    }


    @Override
    public String toString() {
        return String.format(
            "[%s] %s → %s | %s - %s | Train: %s | Days: %s | 1st: €%.2f | 2nd: €%.2f",
            routeId,
            departureCity.getName(),
            arrivalCity.getName(),
            departureTime,
            arrivalTime,
            trainType,
            daysOfOperation,
            firstClassRate,
            secondClassRate
        );
    }
    
}

