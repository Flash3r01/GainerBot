package gainerbot.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpService {
    private static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public String requestJsonString(URI address){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(address)
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            //TODO Error Log
            System.out.println("Error while getting the json data");
        }

        return null;
    }

    public String requestGeneric(URI address){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(address)
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return response.body();
            }
        } catch (InterruptedException | IOException e) {
            //TODO Error Log
            System.out.println("Error while getting the request.");
        }
        return null;
    }
}
