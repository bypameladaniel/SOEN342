import java.util.*;

public class UserInterface {

    public static void printWelcome() {
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
        while (true) {
            System.out.printf(formatedString,
                    sq.getDepartureCity() == null ? "*" : sq.getDepartureCity(),
                    sq.getArrivalCity() == null ? "*" : sq.getArrivalCity(),
                    sq.getDepartureTime() == null ? "*" : sq.getDepartureTime(),
                    sq.getArrivalTime() == null ? "*" : sq.getArrivalTime(),
                    sq.getTrainType() == null ? "*" : sq.getTrainType(),
                    sq.getDaysOfWeek() == null ? "*" : sq.getDaysOfWeek(),
                    sq.getFirstClassRate() == 0 ? "*" : sq.getFirstClassRate(),
                    sq.getSecondClassRate() == 0 ? "*" : sq.getSecondClassRate());
            input = sc.nextLine();

            switch (input) {
                case "0":
                    System.exit(0);
                case "1":
                    sq.setDepartureCity(SearchQuery.getCityInput(cityDB));
                    break;
                case "2":
                    sq.setArrivalCity(SearchQuery.getCityInput(cityDB));
                    break;
                case "3":
                    sq.setDepartureTime(SearchQuery.getTimeInput());
                    break;
                case "4":
                    sq.setArrivalTime(SearchQuery.getTimeInput());
                    break;
                case "5":
                    sq.setTrainType(SearchQuery.getNoConstrainInput());
                    break;
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
                case "7":
                    sq.setFirstClassRate(SearchQuery.getCurrencyInput());
                    break;
                case "8":
                    sq.setSecondClassRate(SearchQuery.getCurrencyInput());
                    break;
                case "search":
                    return sq;
            }

        }
    }

    public static int[] getSortingPreferences() {
        Scanner sc = new Scanner(System.in);
        int[] result = new int[2];

        String inputStr;
        System.out.println("Results found, do you want to sort the trips (y/n)");
        inputStr = sc.next();
        if (!inputStr.equals("y")) {
            return new int[] { 3, 1 };
        }

        whileLoop: while (true) {
            System.out.println("""
                    Please enter the parameter you want to sort by:
                    [1] First Class Rate
                    [2] Second Class Rate
                    [3] Trip Duration
                    [0] Exit
                    """);

            inputStr = sc.next();
            switch (inputStr) {
                case "1":
                    result[0] = 1;
                    break whileLoop;
                case "2":
                    result[0] = 2;
                    break whileLoop;
                case "3", "0":
                    result[0] = 3;
                    break whileLoop;
                default: {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }

        whileLoop: while (true) {
            System.out.println("""
                    Please enter how you want to sort:
                    [1] Ascending
                    [2] Descending
                    [0] Exit
                    """);

            inputStr = sc.next();
            switch (inputStr) {
                case "1", "0":
                    result[1] = 1;
                    break whileLoop;
                case "2":
                    result[1] = 0;
                    break whileLoop;
                default: {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }

        return result;
    }

    public static void printResult(List<Trip> results) {
        int i = 0;
        for (Object obj : results) {
            System.out.println(i + ". " + obj.toString());
            i++;
        }
    }

    public static int getSelectedTripIndex(List<Trip> sortedResults) {
        Scanner sc = new Scanner(System.in);
        int tripIndex = -1;
        System.out.println("Would you like to book one of the listed trips? (y/n)");
        String bookingChoice = sc.next();
        if (!bookingChoice.equals("y")) {
            return -1;
        }
        System.out.print("Enter the number of the trip you want to select: ");

        while (true) {
            String inputStr = sc.next();

            try {
                tripIndex = Integer.parseInt(inputStr);

                if (tripIndex < 0 || tripIndex >= sortedResults.size()) {
                    System.out.println("Invalid trip number. Please enter a number between 0 and "
                            + (sortedResults.size() - 1) + ".");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return tripIndex;
    }

    public static void getClientInfo(Booking booking) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the number of clients:");
        int nbOfClients = -1;
        while (true) {
            String inputStr = sc.next();
            try {
                nbOfClients = Integer.parseInt(inputStr);
                if (nbOfClients < 0) {
                    System.out.println("Invalid trip number. Please enter a number bigger than 0.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        for (int i = 0; i < nbOfClients; i++) {
            // Create logic for enterTravelerDetails(age, name, id) somewhere and call it
            // here
            // Follow operation contract when creating its logic
            System.out.println("Logic for entering client: " + (i + 1) + "...");
        }

    }

}
