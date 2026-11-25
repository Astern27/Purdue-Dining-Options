import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    private final static String[] locations = {"Hillenbrand", "Earhart", "Wiley", "Windsor", "Ford"};
    public static void main(String[] args) {
        try {
            //I'm using scanner so that you do not need an updated JDK to run the program
            Scanner scanner = new Scanner(System.in);
            System.out.println("Date: (format: YYYY-MM-DD) (Leave blank for today)");
            String dateInput = scanner.nextLine();
            System.out.println("Time: (format: HH:MM (Use Military Time)) (Leave blank for now)");
            String timeInput = scanner.nextLine();
            System.out.println("What item would you like to search for? (Leave blank to show everything)");
            String keyword = scanner.nextLine().toLowerCase();
            LocalDate date;
            LocalTime time;
            //Today should be the default date
            if (dateInput.isBlank()) {
                date = LocalDate.now();
            } else {
                date = LocalDate.parse(dateInput);
            }
            //Now should be the default time
            if (timeInput.isBlank()) {
                time = LocalTime.now();
            } else {
                time = LocalTime.parse(timeInput);
            }
            HttpClient client = HttpClient.newHttpClient();
            for (String location : locations) {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.hfs.purdue.edu/menus/v2/locations/" + location + "/" + date))
                    .GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject menu = JsonParser.parseString(response.body()).getAsJsonObject();
                printMenuItems(menu, time, keyword);
            }
            HttpRequest request = HttpRequest.newBuilder()
                //I have to use new URI() instead of URI.create() because the URL has spaces in it. URI.create() crashes when there are spaces in the URL.
                .uri(new URI("https", "api.hfs.purdue.edu", "/menus/v2/locations/1bowl at Meredith Hall/" + date, null))
                .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject menu = JsonParser.parseString(response.body()).getAsJsonObject();
            printMenuItems(menu, time, keyword);
        } catch (InterruptedException e) {
            System.out.println("Request interrupted: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } catch (URISyntaxException e) {
            System.out.println("URI Syntax Error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse date/time. Use the correct format");
        }
    }

    //Prints out the menu items from a menu. Separates the items into their stations using indentations
    public static void printMenuItems(JsonObject menu, LocalTime time, String keyword) {
        //Instead of directly printing, I add print contents to a list to print at the end
        ArrayList<String> prints = new ArrayList<String>();
        prints.add(menu.get("Location").getAsString());
        JsonArray meals = menu.getAsJsonArray("Meals");
        boolean isOpen = false;
        for (int m = 0; m < meals.size(); m++) {
            JsonObject meal = meals.get(m).getAsJsonObject();
            //I have to check that a meal is open before getting the hours because hours are null when status is closed
            if (meal.get("Status").getAsString().equals("Open")) {
                JsonObject hours = meal.getAsJsonObject("Hours");
                //Instead of using is after start and is before end, I use not after end and not before start so the start and end times are inclusive
                if (!time.isAfter(LocalTime.parse(hours.get("EndTime").getAsString())) && !time.isBefore(LocalTime.parse(hours.get("StartTime").getAsString()))) {
                    JsonArray stations = meal.getAsJsonArray("Stations");
                    for (int s = 0; s < stations.size(); s++) {
                        JsonObject station = stations.get(s).getAsJsonObject();
                        prints.add("  " + station.get("Name").getAsString());
                        int numItems = 0;
                        JsonArray items = station.getAsJsonArray("Items");
                        for (int i = 0; i < items.size(); i++) {
                            JsonObject item = items.get(i).getAsJsonObject();
                            if (keyword.equals("") || item.get("Name").getAsString().toLowerCase().indexOf(keyword) != -1) {
                                prints.add("    " + item.get("Name").getAsString());
                                numItems++;
                            }
                        }
                        if (numItems == 0) {
                            //Does not print the name of a station if there are no meals being printed
                            prints.removeLast();
                        } else {
                            isOpen = true;
                        }
                    }
                }
            }
        }
        if (!isOpen) {
            //Does not print the name of a location if no meals are being printed
            prints.removeLast();
        }
        for (int i = 0; i < prints.size(); i++) {
            System.out.println(prints.get(i));
        }
        if (prints.size() == 0) {
            System.out.println(menu.get("Location").getAsString() + ": No items available that meet your search");
        }
    }

    /*
    Hierarchy of a Purdue Menu:
    (Object) Menu
    - Location — String
    - Date — String
    - Meals — Array
        (Object) Meal
        - Name — String
        - Status — String
        - Hours — Object (or null if no meal)
            - StartTime — String
            - EndTime — String
        - Stations — Array
            (Object) Station
            - Name — String
            - Items — Array
                (Object) Menu Item
                - Name — String
    */
}