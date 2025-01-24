package by.raznatovskij.http;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class HttpServer {
    private final int port;


    public HttpServer(int port) {
        this.port = port;

    }
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            while(true) {
                try (Socket socket = serverSocket.accept()) {
                    processSocket(socket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String readRequestBody(InputStream input, int contentLength) throws IOException {
        byte[] body = new byte[contentLength];
        input.read(body);
        return new String(body);
    }

    private void processSocket(Socket socket) {
        try (socket;
             var input = new BufferedInputStream(socket.getInputStream());
             var output = new BufferedOutputStream(socket.getOutputStream())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            int contentLength = 0;


            while (!(line = reader.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }
            String requestBody = readRequestBody(input,contentLength);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(requestBody);


            JsonNode employees = node.get("employees");
            int totalIncome = 0;
            int totalTax = 0;

            for (JsonNode employee : employees) {
                int salary = employee.get("salary").asInt();
                int tax = employee.get("tax").asInt();
                totalIncome += salary;
                totalTax += tax;
            }

            int totalProfit = totalIncome - totalTax;

            byte[] bytes = Files.readAllBytes(Path.of("src/main/resources/example.html"));
            String htmlResponse = new String(bytes, StandardCharsets.UTF_8);

            String htmlContent = String.format(htmlResponse, totalIncome, totalTax, totalProfit);
            output.write("""
                    HTTP/1.1 200 OK
                    Content-Type: text/html
                    Content-Length: %s 
                    """.formatted(htmlContent.getBytes().length).getBytes());
            output.write(System.lineSeparator().getBytes());
            output.write(htmlContent.getBytes());
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
