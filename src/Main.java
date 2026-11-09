import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Main {
    private static String[] locations = {"Hillenbrand", "Earhart", "Wiley", "Windsor", "Ford"};
    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            for (String location : locations) {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.hfs.purdue.edu/menus/v2/locations/" + location + "/2026-11-07"))
                    .GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject menu = JsonParser.parseString(response.body()).getAsJsonObject();
                printMenuItems(menu);
            }
            HttpRequest request = HttpRequest.newBuilder()
                //I have to use new URI() instead of URI.create() because the URL has spaces in it. URI.create() crashes when there are spaces in the URL.
                .uri(new URI("https", "api.hfs.purdue.edu", "/menus/v2/locations/1bowl at Meredith Hall/2026-11-07", null))
                .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject menu = JsonParser.parseString(response.body()).getAsJsonObject();
            printMenuItems(menu);
        } catch (InterruptedException e) {
            System.out.println("Request interrupted: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } catch (URISyntaxException e) {
            System.out.println("URI Syntax Error: " + e.getMessage());
        }
    }

    //Prints out the menu items from a menu. Separates the items into their stations using indentations
    public static void printMenuItems(JsonObject menu) {
        System.out.println(menu.get("Location").getAsString());
        JsonArray meals = menu.getAsJsonArray("Meals");
        for (int m = 0; m < meals.size(); m++) {
            JsonObject meal = meals.get(m).getAsJsonObject();
            if (meal.get("Status").getAsString().equals("Open")) {
                JsonArray stations = meal.getAsJsonArray("Stations");
                for (int s = 0; s < stations.size(); s++) {
                    JsonObject station = stations.get(s).getAsJsonObject();
                    System.out.println("  " + station.get("Name").getAsString());
                    JsonArray items = station.getAsJsonArray("Items");
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        System.out.println("    " + item.get("Name").getAsString());
                    }
                }
            }
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
        - Stations — Array
            (Object) Station
            - Name — String
            - Items — Array
                (Object) Menu Item
                - Name — String
    */
}