package by.davlar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static java.net.http.HttpRequest.BodyPublishers.ofFile;


public class HttpClientRunner {
    private static final String WEB_ADDRESS = "http://localhost:8082";

    private static final String CONTENT_TYPE = "application/json";
    private static final String JSON_PATH = "src/main/resources/client.json";

    private static final String ANSWER_PATH = "src/main/answer.html";

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        var request = HttpRequest.newBuilder()
                .uri(URI.create(WEB_ADDRESS))
                .header("content-type", CONTENT_TYPE)
                .POST(ofFile(Path.of(JSON_PATH)))
                .build();

        var response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.get().headers());
        String body = response.get().body();
        System.out.println(body);
        try (FileWriter fileWriter = new FileWriter(ANSWER_PATH)) {
            fileWriter.write(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
