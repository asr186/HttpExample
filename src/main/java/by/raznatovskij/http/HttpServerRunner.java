package by.raznatovskij.http;
public class HttpServerRunner {
    public static void main(String[] args) {
        var httpServer = new HttpServer(8082);
        httpServer.run();

    }
}
