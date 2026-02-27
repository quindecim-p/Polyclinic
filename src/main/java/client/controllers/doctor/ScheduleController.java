package client.controllers.doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.entities.WorkDay;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ScheduleController {

    @FXML
    private ListView<String> scheduleList;
    @FXML
    private Button backButton;

    private List<WorkDay> workDays;

    private final Map<DayOfWeek, String> dayTranslations = new EnumMap<>(DayOfWeek.class);

    public ScheduleController() {
        dayTranslations.put(DayOfWeek.MONDAY, "Понедельник");
        dayTranslations.put(DayOfWeek.TUESDAY, "Вторник");
        dayTranslations.put(DayOfWeek.WEDNESDAY, "Среда");
        dayTranslations.put(DayOfWeek.THURSDAY, "Четверг");
        dayTranslations.put(DayOfWeek.FRIDAY, "Пятница");
        dayTranslations.put(DayOfWeek.SATURDAY, "Суббота");
        dayTranslations.put(DayOfWeek.SUNDAY, "Воскресенье");
    }

    @FXML
    public void initialize() {
        loadSchedule();
    }

    private void loadSchedule() {
        int doctorId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_WORKING_SCHEDULE, String.valueOf(doctorId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                    .create();

            workDays = gson.fromJson(response.getMessage(), new TypeToken<List<WorkDay>>() {}.getType());
            populateScheduleList();
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка получения данных расписания");
        }
    }

    private void populateScheduleList() {
        scheduleList.getItems().clear();
        if (workDays == null || workDays.isEmpty()) {
            scheduleList.getItems().add("Расписание не назначено");
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (WorkDay workDay : workDays) {
            String dayName = dayTranslations.getOrDefault(workDay.getDay(), "Неизвестный день");
            String dayInfo = String.format("%s: %s - %s",
                    dayName,
                    workDay.getStartTime().format(timeFormatter),
                    workDay.getEndTime().format(timeFormatter));
            scheduleList.getItems().add(dayInfo);
        }
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.DOCTOR_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}