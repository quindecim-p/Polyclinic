package server.utils;

import com.google.gson.Gson;
import common.utils.Request;
import common.utils.Response;
import server.services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Gson gson;
    private final BufferedReader input;
    private final PrintWriter output;

    private final RequestHandler requestHandler;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.gson = new Gson();
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);

        UserService userService = new UserService();
        PatientService patientService = new PatientService();
        DoctorService doctorService = new DoctorService();
        MedicalCardService medicalCardService = new MedicalCardService();
        AppointmentService appointmentService = new AppointmentService();
        DiagnosisService diagnosisService = new DiagnosisService();
        PrescriptionService prescriptionService = new PrescriptionService();
        ReferenceService referenceService = new ReferenceService();
        WorkDayService workDayService = new WorkDayService();
        WorkingScheduleService workingScheduleService = new WorkingScheduleService();

        this.requestHandler = new RequestHandler(userService, patientService, doctorService, medicalCardService,
                appointmentService, diagnosisService, prescriptionService, referenceService, workDayService, workingScheduleService);
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = input.readLine();

                Request request = gson.fromJson(message, Request.class);

                Response response = requestHandler.handleRequest(request);

                sendResponse(response);
            }
        } catch (IOException e) {
            System.out.println("Клиент " + clientSocket.getInetAddress().getHostAddress() + " отключился");
        } finally {
            try {
                ServerHandler.removeClient(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendResponse(Response response) {
        String responseMessage = gson.toJson(response);
        System.out.println("Отправляем ответ клиенту: " + responseMessage);
        output.println(responseMessage);
        output.flush();
    }
}