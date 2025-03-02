package by.raznatovskij.http;

import org.jsoup.Jsoup;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;


import static java.net.http.HttpRequest.BodyPublishers.ofFile;

public class HttpClientRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

       var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082"))
                .header("content-type","application/json")
                .POST(ofFile(Path.of("src/main/resources/example.json")))
                .build();

      var response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(response.headers());
        System.out.println(response.body());


        String htmlFilePath = "d:\\JavaEnterprise\\example.html";
        try (FileWriter writer = new FileWriter(htmlFilePath)) {
            writer.write(response.headers().toString());
            writer.write(response.body().toString());
        }
        System.out.println("HTML файл успешно сохранен в " + htmlFilePath);
    }
}
