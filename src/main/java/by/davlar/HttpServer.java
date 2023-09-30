package by.davlar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpHandlers;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class HttpServer {

    private static final String ANSWER_PATH = "src/main/resources/answer.html";

    private int port;
    private ExecutorService executorService;

    public HttpServer(int port, int threadPool) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadPool);
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                var socket = serverSocket.accept();
                executorService.submit(() -> processedSocked(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void processedSocked(Socket socket) {
        try (socket;
             var inputStream = new DataInputStream(socket.getInputStream());
             var outputStream = new DataOutputStream(socket.getOutputStream())) {

            var bf = new BufferedReader(new InputStreamReader(inputStream));
            String strCurrentLine;
            StringBuilder sb = new StringBuilder();
            while (!((strCurrentLine = bf.readLine()).isEmpty())) {
                sb.append(strCurrentLine);
                sb.append("\n");
                System.out.println(strCurrentLine);
            }
            HeaderParser hp = new HeaderParser(sb.toString());

            byte[] bytes = inputStream.readNBytes(
                    Integer.parseInt(hp.findValue("content-length"))
            );
            System.out.println(new String(bytes));
            ObjectMapper mapper = new ObjectMapper();
            List<Person> data = mapper.readValue(bytes, SalaryInfo.class).getEmployees();

            int salary = data.stream().mapToInt(Person::getSalary).sum();
            int tax = data.stream().mapToInt(Person::getTax).sum();
            int profit = salary - tax;

            byte[] body = Files.readAllBytes(Path.of(ANSWER_PATH));
            body = new String(body)
                    .replace("${total_income}", String.valueOf(salary))
                    .replace("${total_tax}", String.valueOf(tax))
                    .replace("${total_profit}", String.valueOf(profit))
                    .getBytes();

            outputStream.write(
                    """
                            HTTP/1.1 200 OK
                            content-type: text/html
                            content-length: %s
                            """.formatted(body.length).getBytes()
            );
            outputStream.write(System.lineSeparator().getBytes());
            outputStream.write(body);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
