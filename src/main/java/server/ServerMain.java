package server;

import server.utils.ServerHandler;

public class ServerMain {
    public static void main(String[] args) {
        ServerHandler server = new ServerHandler();
        Thread serverThread = new Thread(server);
        serverThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
    }
}