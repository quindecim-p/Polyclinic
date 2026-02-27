package server.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler implements Runnable {
    private static final int PORT = 8888;
    private static final List<Socket> clients = new ArrayList<>();
    private ServerSocket serverSocket;

    public ServerHandler() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Сервер запущен на порту " + PORT);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое подключение: " + clientSocket.getInetAddress().getHostAddress());
                clients.add(clientSocket);

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeClient(Socket clientSocket) {
        clients.remove(clientSocket);
    }

    public void stopServer() {
        System.out.println("Завершаем работу сервера...");

        for (Socket client : clients) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        clients.clear();

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Серверный сокет закрыт.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
