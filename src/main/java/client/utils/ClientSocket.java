package client.utils;

import com.google.gson.Gson;
import common.utils.Request;
import common.utils.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {
    private static final ClientSocket SINGLE_INSTANCE = new ClientSocket();

    private static Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private final Gson gson = new Gson();

    private ClientSocket() {
        try {
            socket = new Socket("localhost", 8888);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(Request request) {
        try {
            String jsonRequest = new Gson().toJson(request);
            output.println(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response receiveResponse() {
        try {
            String jsonResponse = input.readLine();
            if (jsonResponse != null) {
                return gson.fromJson(jsonResponse, Response.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ClientSocket getInstance() { return SINGLE_INSTANCE; }

    public Socket getSocket() { return socket; }

    public void setSocket(Socket socket) { ClientSocket.socket = socket; }

    public BufferedReader getInput() { return input; }

    public void setInput(BufferedReader input) { this.input = input; }

    public PrintWriter getOutput() { return output; }

    public void setOutput(PrintWriter output) { this.output = output; }

}