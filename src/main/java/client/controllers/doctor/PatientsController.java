package client.controllers.doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import common.entities.Patient;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.Request;
import common.utils.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PatientsController {

    @FXML
    private TableView<Patient> patientsTable;
    @FXML
    private TableColumn<Patient, String> nameColumn;
    @FXML
    private TableColumn<Patient, String> cardNumberColumn;
    @FXML
    private TextField searchField;
    @FXML
    private TextArea patientDetailsText;
    @FXML
    private Button backButton;

    private List<Patient> patients;

    @FXML
    public void initialize() {
        setupTable();
        loadPatients();
        setupSearch();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(
                param.getValue().getPersonData().getSurname() + " " + param.getValue().getPersonData().getName()
        ));

        cardNumberColumn.setCellValueFactory(param ->
                new SimpleStringProperty(String.valueOf(param.getValue().getMedicalCard().getId()))
        );

        patientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showPatientDetails(newSelection);
            }
        });
    }

    private void loadPatients() {
        int doctorId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_PATIENTS, String.valueOf(doctorId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            patients = new Gson().fromJson(response.getMessage(), new TypeToken<List<Patient>>() {}.getType());
            populatePatientsTable();
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка получения данных");
        }
    }

    private void populatePatientsTable() {
        if (patients == null || patients.isEmpty()) {
            showAlert("Нет пациентов");
            return;
        }
        ObservableList<Patient> observablePatients = FXCollections.observableArrayList(patients);
        patientsTable.setItems(observablePatients);
    }

    private void showPatientDetails(Patient patient) {
        String details = "Фамилия: " + patient.getPersonData().getSurname() + "\n" +
                "Имя: " + patient.getPersonData().getName() + "\n" +
                "Отчество: " + patient.getPersonData().getPatronymic() + "\n" +
                "Номер карты: " + patient.getMedicalCard().getId() + "\n" +
                "Телефон: " + patient.getPersonData().getPhone() + "\n" +
                "Email: " + patient.getPersonData().getEmail() + "\n" +
                "Адрес: " + patient.getPersonData().getAddress() + "\n" +
                "Дата рождения: " + patient.getPersonData().getBirthDate();
        patientDetailsText.setText(details);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Patient> filtered = patients.stream()
                    .filter(patient ->
                            patient.getPersonData().getSurname().toLowerCase().contains(newValue.toLowerCase()) ||
                                    String.valueOf(patient.getMedicalCard().getId()).contains(newValue))
                    .collect(Collectors.toList());
            patientsTable.setItems(FXCollections.observableArrayList(filtered));
        });
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