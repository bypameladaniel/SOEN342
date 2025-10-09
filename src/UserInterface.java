import java.util.*;

public class UserInterface {

    public static void printWelcome(){
    System.out.println("""
         ************************************************
         *   Welcome to the railway trip search engine  *
         ************************************************
         """);

    System.out.println("Loading trip information...");

    }

    public static SearchQuery printSearchParameters(CityDB cityDB) {
        Scanner sc = new Scanner(System.in);
        SearchQuery sq = new SearchQuery();


        String formatedString = """
                 Select a parameter to modify the search querry, else enter '0' to quit or 'search' to search:
                 [1] Departure City: %s
                 [2] Arrival City: %s
                 [3] Departure Time: %s
                 [4] Arrival Time: %s
                 [5] Train Type: %s
                 [6] Days of Operation: %s
                 [7] First Class Rate: %s
                 [8] Second Class Rate: %s
                 [0] Quit
                 [search] to search
                 """;


        String input = "";
        while(true){
            System.out.printf(formatedString,
                 sq.getDepartureCity() == null? "*":sq.getDepartureCity(),
                 sq.getArrivalCity() == null? "*":sq.getArrivalCity(),
                 sq.getDepartureTime() == null? "*":sq.getDepartureTime(),
                 sq.getArrivalTime() == null? "*":sq.getArrivalTime(),
                 sq.getTrainType() == null? "*":sq.getTrainType(),
                 sq.getDaysOfWeek() == null? "*":sq.getDaysOfWeek(),
                 sq.getFirstClassRate() == 0? "*":sq.getFirstClassRate(),
                 sq.getSecondClassRate() == 0? "*":sq.getSecondClassRate()
                 );
            input = sc.nextLine();

            switch(input){
                case "0": System.exit(0);
                case "1": sq.setDepartureCity(SearchQuery.getCityInput(cityDB)); break;
                case "2": sq.setArrivalCity(SearchQuery.getCityInput(cityDB)); break;
                case "3": sq.setDepartureTime(SearchQuery.getTimeInput()); break;
                case "4": sq.setArrivalTime(SearchQuery.getTimeInput()); break;
                case "5": sq.setTrainType(SearchQuery.getNoConstrainInput()); break;
                case "6": {
                 System.out.println("Do you want to 'add' or 'remove' a day?");
                 input = sc.nextLine();
                 if (input.equalsIgnoreCase("remove")) {
                     sq.removeDayInput();
                 } else {
                     sq.addDayInput();
                 }
                 break;
                }
                case "7": sq.setFirstClassRate(SearchQuery.getCurrencyInput()); break;
                case "8": sq.setSecondClassRate(SearchQuery.getCurrencyInput()); break;
                case "search": return  sq;
            }

        }
    }

    public static void printResult(List<Object> results){
        for (Object obj : results){
            System.out.println(obj.toString());
        }
    }
}

