import java.time.LocalDate;
import java.util.*;

public class UserInterface {

    private static final Scanner sc = new Scanner(System.in);

    public static void printWelcome() {
        System.out.println("""
                ************************************************
                *   Welcome to the railway trip search engine  *
                ************************************************
                """);

        System.out.println("Loading trip information...");

    }

    public static void printMenu() {
        System.out.println("\n==== MAIN MENU ====");
        System.out.println("1. Search and book a trip");
        System.out.println("2. View all bookings");
        System.out.println("3. Exit");
    }

    public static SearchQuery printSearchParameters(CityDB cityDB) {
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
                    return null;
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
        int[] result = new int[2];

        String inputStr;
        System.out.println("Results found, do you want to sort the trips (y/n)");
        inputStr = sc.nextLine();
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

            inputStr = sc.nextLine();
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

            inputStr = sc.nextLine();
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
        int tripIndex = -1;
        System.out.println("Would you like to book one of the listed trips? (y/n)");
        String bookingChoice = sc.nextLine();
        if (!bookingChoice.equals("y")) {
            return -1;
        }
        System.out.print("Enter the number of the trip you want to select: ");

        while (true) {
            String inputStr = sc.nextLine();

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

    public static int getNbOfClients(Booking booking) {
        System.out.println("Please enter the number of clients:");
        int nbOfClients = -1;
        while (true) {
            String inputStr = sc.nextLine();
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

        return nbOfClients;
    }

    public static String promptClientFirstName() {

        System.out.print("Enter First Name: ");
        String name = sc.nextLine();
        return name;
    }

    public static String promptClientLastName() {

        System.out.print("Enter Last Name: ");
        String name = sc.nextLine();
        return name;
    }

    public static int promptClientAge() {

        int age = -1;

        while (true) {
            System.out.print("Enter Age: ");
            String ageStr = sc.nextLine();

            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0) {
                    System.out.println("Invalid age. Please enter a number greater than 0.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for age.");
            }
        }
        return age;
    }

    public static Long promptClientId() {

        long id = -1;
        while (true) {
            System.out.print("Enter ID: ");
            String idStr = sc.nextLine();

            try {
                id = Long.parseLong(idStr);
                if (id <= 0) {
                    System.out.println("Invalid ID. Please enter a number greater than 0.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for ID.");
            }
        }
        return id;
    }

    public static LocalDate promptTripDate() {
        while (true) {
        System.out.print("Enter Date of Travel (YYYY-MM-DD): ");
        String dateStr = sc.nextLine();
        try {
            return  LocalDate.parse(dateStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
        }
        }
    }
    public static boolean confirmBooking() {
        System.out.println("Would you like to confirm your booking? (y/n)");
        String clientChoice = sc.nextLine();
        if (clientChoice.equals("y")) {
            return true;
        } else {
            return false;
        }
    }

    public static Client promptClientLookup(ClientDB clientDB) {
        Client c = null;
        String idStr;
        String nameStr;
        while (true) {
            System.out.print("Enter Client ID: ");
            idStr = sc.nextLine();

            if (idStr.equals("0")) {return null;}

            System.out.println("Enter Client Last Name: ");
            nameStr = sc.nextLine();

            try {
                Long clientID = Long.parseLong(idStr);;
                c = clientDB.finClientByIDAndName(clientID, nameStr);// Placeholder, actual lookup should be done outside
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for Client ID.");
            }

            if (c != null) {
                return c;
            } else {
                System.out.println("Client not found. Please try again. or enter '0' to exit.");
            }
        }
    }
}
