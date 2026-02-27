package client.controllers.patient;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.dto.WorkDayDTO;
import server.dto.DoctorDTO;
import common.enums.SceneRoute;
import common.enums.errors.ApplicationError;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.enums.types.SpecializationType;
import common.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulesController {

    @FXML
    private ComboBox<String> specializationFilter;
    @FXML
    private ComboBox<String> dayFilter;
    @FXML
    private Spinner<LocalTime> startTimeFilter;
    @FXML
    private Spinner<LocalTime> endTimeFilter;
    @FXML
    private ComboBox<String> sortOptions;
    @FXML
    private ListView<String> doctorsList;
    @FXML
    private TextArea scheduleArea;
    @FXML
    private Button backButton;

    private List<DoctorDTO> doctors;
    private List<DoctorDTO> filteredDoctors;

    @FXML
    public void initialize() {
        setupFilters();
        loadDoctorsData();
        setupListViewListener();
    }

    private void setupFilters() {
        setupComboBox(specializationFilter, "Все", Arrays.stream(SpecializationType.values())
                .map(SpecializationType::getDescription).toList());

        setupComboBox(dayFilter, "Все", List.of("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"));

        setupComboBox(sortOptions, "По фамилии", List.of("По фамилии", "По специализации", "По времени работы"));

        setupTimeSpinners();

        specializationFilter.setOnAction(e -> filterDoctors());
        dayFilter.setOnAction(e -> filterDoctors());
        sortOptions.setOnAction(e -> filterDoctors());
        startTimeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterDoctors());
        endTimeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterDoctors());
    }

    private void setupComboBox(ComboBox<String> comboBox, String defaultValue, List<String> values) {
        comboBox.getItems().add(defaultValue);
        comboBox.getItems().addAll(values);
        comboBox.setValue(defaultValue);
    }

    private void setupTimeSpinners() {
        setupSpinner(startTimeFilter, LocalTime.of(8, 0));
        setupSpinner(endTimeFilter, LocalTime.of(18, 0));
    }

    private void setupSpinner(Spinner<LocalTime> spinner, LocalTime defaultValue) {
        SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<>() {
            private final LocalTime min = LocalTime.of(0, 0);
            private final LocalTime max = LocalTime.of(23, 59);

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 15L).isBefore(min) ? min : getValue().minusMinutes(steps * 15L));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 15L).isAfter(max) ? max : getValue().plusMinutes(steps * 15L));
            }
        };
        spinner.setValueFactory(factory);
        spinner.getValueFactory().setValue(defaultValue);
    }

    private void loadDoctorsData() {
        Request request = new Request(RequestType.GET_SCHEDULES, null);
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(response == null ? ApplicationError.UNEXPECTED_ERROR.getMessage() : response.getMessage());
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        doctors = gson.fromJson(response.getMessage(), new TypeToken<List<DoctorDTO>>() {}.getType());
        filteredDoctors = new ArrayList<>(doctors);
        populateDoctorsList(filteredDoctors);
    }

    private void populateDoctorsList(List<DoctorDTO> doctorsToDisplay) {
        doctorsList.getItems().clear();

        if (doctorsToDisplay == null || doctorsToDisplay.isEmpty()) {
            doctorsList.getItems().add("Нет данных о врачах");
            return;
        }

        for (DoctorDTO doctor : doctorsToDisplay) {
            doctorsList.getItems().add(String.format("%s (%s)", doctor.getSurname(), doctor.getSpecialization()));
        }
    }

    private void setupListViewListener() {
        doctorsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (filteredDoctors != null && index >= 0 && index < filteredDoctors.size()) {
                displayDoctorSchedule(filteredDoctors.get(index));
            }
        });
    }

    private void displayDoctorSchedule(DoctorDTO doctor) {
        StringBuilder scheduleText = new StringBuilder();
        scheduleText.append(String.format("Фамилия: %s\nСпециализация: %s\nКабинет: %d\n\nРасписание:\n",
                doctor.getSurname(), doctor.getSpecialization(), doctor.getOfficeNumber()));

        if (doctor.getWorkDays() != null && !doctor.getWorkDays().isEmpty()) {
            for (var workDay : doctor.getWorkDays()) {
                String dayName = capitalize(workDay.getDay().getDisplayName(TextStyle.FULL, new Locale("ru")));
                scheduleText.append(String.format("%s: %s - %s\n",
                        dayName, workDay.getStartTime(), workDay.getEndTime()));
            }
        } else {
            scheduleText.append("Нет расписания");
        }

        scheduleArea.setText(scheduleText.toString());
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private void filterDoctors() {
        filteredDoctors = doctors.stream()
                .filter(this::filterBySpecialization)
                .filter(this::filterByDay)
                .filter(this::filterByTime)
                .sorted(this::compareDoctors)
                .collect(Collectors.toList());

        populateDoctorsList(filteredDoctors);
    }

    private boolean filterBySpecialization(DoctorDTO doctor) {
        String selected = specializationFilter.getValue();
        return "Все".equals(selected) || doctor.getSpecialization().equals(selected);
    }

    private boolean filterByDay(DoctorDTO doctor) {
        String selectedDay = dayFilter.getValue();

        if ("Все".equals(selectedDay)) {
            return true;
        }

        DayOfWeek selectedDayOfWeek = convertStringToDayOfWeek(selectedDay);

        return doctor.getWorkDays().stream()
                .anyMatch(workDay -> workDay.getDay().equals(selectedDayOfWeek));
    }

    private DayOfWeek convertStringToDayOfWeek(String dayName) {
        return switch (dayName) {
            case "Понедельник" -> DayOfWeek.MONDAY;
            case "Вторник" -> DayOfWeek.TUESDAY;
            case "Среда" -> DayOfWeek.WEDNESDAY;
            case "Четверг" -> DayOfWeek.THURSDAY;
            case "Пятница" -> DayOfWeek.FRIDAY;
            case "Суббота" -> DayOfWeek.SATURDAY;
            case "Воскресенье" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Некорректное значение дня недели: " + dayName);
        };
    }


    private boolean filterByTime(DoctorDTO doctor) {
        LocalTime filterStartTime = startTimeFilter.getValue();
        LocalTime filterEndTime = endTimeFilter.getValue();

        return doctor.getWorkDays().stream().anyMatch(wd ->
                wd.getStartTime().isBefore(filterEndTime) && wd.getEndTime().isAfter(filterStartTime));
    }

    private int compareDoctors(DoctorDTO d1, DoctorDTO d2) {
        return switch (sortOptions.getValue()) {
            case "По фамилии" -> d1.getSurname().compareTo(d2.getSurname());
            case "По специализации" -> d1.getSpecialization().compareTo(d2.getSpecialization());
            case "По времени работы" -> SchedulesController.compareWorkTimes(d1, d2);
            default -> 0;
        };
    }

    private static int compareWorkTimes(DoctorDTO d1, DoctorDTO d2) {
        LocalTime d1Start = d1.getWorkDays().stream()
                .map(WorkDayDTO::getStartTime)
                .min(LocalTime::compareTo)
                .orElse(LocalTime.MAX);
        LocalTime d2Start = d2.getWorkDays().stream()
                .map(WorkDayDTO::getStartTime)
                .min(LocalTime::compareTo)
                .orElse(LocalTime.MAX);
        return d1Start.compareTo(d2Start);
    }

    @FXML
    private void Back_Pressed() throws IOException {
        SceneSwitcher.switchScene((Stage) backButton.getScene().getWindow(), SceneRoute.PATIENT_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}