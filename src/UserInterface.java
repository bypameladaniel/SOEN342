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

    public static int[] getSortingPreferences(){
        Scanner sc = new Scanner(System.in);
        int[] result = new int[2];

        String inputStr;
        System.out.println("Results found, do you want to sort the trips (y/n)");
        inputStr = sc.next();
        if(!inputStr.equals("y")) {
            return new int[]{3,1};
        }

        whileLoop:
        while(true) {
            System.out.println("""
                Please enter the parameter you want to sort by:
                [1] First Class Rate
                [2] Second Class Rate
                [3] Trip Duration
                [0] Exit
                """);

            inputStr = sc.next();
            switch (inputStr) {
                case "1": result[0] = 1; break whileLoop;
                case "2": result[0] = 2; break whileLoop;
                case "3","0": result[0] = 3; break whileLoop;
                default:  {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }

        whileLoop:
        while(true) {
            System.out.println("""
                Please enter how you want to sort:
                [1] Ascending
                [2] Descending
                [0] Exit
                """);

            inputStr = sc.next();
            switch (inputStr) {
                case "1","0": result[1] = 1; break whileLoop;
                case "2": result[1] = 0; break whileLoop;
                default:  {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }

        return result;
    }

    public static void printResult(List<Trip> results){
        for (Object obj : results){
            System.out.println(obj.toString());
        }
    }
}

