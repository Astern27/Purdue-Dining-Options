import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public static void main(String[] args) {
    try {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            //I have to use new URI() instead of URI.create() because the URL has spaces in it. URI.create() crashes when there are spaces in the URL.
            .uri(new URI("https", "api.hfs.purdue.edu", "/menus/v2/locations/1bowl at Meredith Hall/2026-11-07", null))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    } catch (InterruptedException e) {
    } catch (IOException e) {
    } catch (URISyntaxException e) {
    }
}

//The 2 ways to create the URL depending on whether it is a dining court or 1bowl
//.uri(URI.create("https://api.hfs.purdue.edu/menus/v2/locations/Wiley/2026-11-07"))
//new URI("https", "api.hfs.purdue.edu", "/menus/v2/locations/1bowl at Meredith Hall/2026-11-07", null)