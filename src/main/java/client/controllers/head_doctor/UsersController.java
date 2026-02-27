package client.controllers.head_doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import common.entities.Doctor;
import common.entities.Patient;
import common.entities.User;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.enums.types.SpecializationType;
import common.utils.Request;
import common.utils.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UsersController {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab usersTab;
    @FXML
    private Tab doctorsTab;
    @FXML
    private Tab patientsTab;

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableView<Doctor> doctorsTable;
    @FXML
    private TableColumn<Doctor, Integer> doctorIdColumn;
    @FXML
    private TableColumn<Doctor, String> specializationColumn;
    @FXML
    private TableColumn<Doctor, Integer> officeNumberColumn;
    @FXML
    private TableColumn<Doctor, String> doctorSurnameColumn;
    @FXML
    private TableColumn<Doctor, String> doctorNameColumn;
    @FXML
    private TableColumn<Doctor, String> doctorPatronymicColumn;
    @FXML
    private TableColumn<Doctor, String> doctorPhoneColumn;
    @FXML
    private TableColumn<Doctor, String> doctorEmailColumn;
    @FXML
    private TableColumn<Doctor, String> doctorAddressColumn;
    @FXML
    private TableColumn<Doctor, String> doctorBirthDateColumn;

    @FXML
    private TableView<Patient> patientsTable;
    @FXML
    private TableColumn<Patient, Integer> patientIdColumn;
    @FXML
    private TableColumn<Patient, String> patientSurnameColumn;
    @FXML
    private TableColumn<Patient, String> patientNameColumn;
    @FXML
    private TableColumn<Patient, String> patientPatronymicColumn;
    @FXML
    private TableColumn<Patient, String> patientPhoneColumn;
    @FXML
    private TableColumn<Patient, String> patientEmailColumn;
    @FXML
    private TableColumn<Patient, String> patientAddressColumn;
    @FXML
    private TableColumn<Patient, String> patientBirthDateColumn;
    @FXML
    private TableColumn<Patient, String> medicalCardColumn;

    @FXML
    private TextField userSearchField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TextField doctorSurnameSearchField;
    @FXML
    private ComboBox<String> specializationComboBox;
    @FXML
    private TextField patientSurnameSearchField;
    @FXML
    private TextField patientMedicalCardSearchField;

    private List<User> allUsers;
    private List<Doctor> allDoctors;
    private List<Patient> allPatients;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        configureUsersTable();
        configureDoctorTable();
        configurePatientTable();
        loadUsers();
        loadDoctors();
        loadPatients();
        setupSearchFields();
        loadSpecializations();
        loadRoles();
    }

    private void configureUsersTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().getRoleType().toString()));
    }

    private void configureDoctorTable() {
        doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        doctorSurnameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getSurname()));
        doctorNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getName()));
        doctorPatronymicColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getPatronymic()));
        doctorPhoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getPhone()));
        doctorEmailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getEmail()));
        doctorAddressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getAddress()));
        doctorBirthDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getBirthDate()));
        specializationColumn.setCellValueFactory(data -> new SimpleStringProperty(SpecializationType.valueOf(data.getValue().getSpecialization().toUpperCase()).getDescription()));
        officeNumberColumn.setCellValueFactory(new PropertyValueFactory<>("officeNumber"));
    }

    private void configurePatientTable() {
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientSurnameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getSurname()));
        patientNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getName()));
        patientPatronymicColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getPatronymic()));
        patientPhoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getPhone()));
        patientEmailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getEmail()));
        patientAddressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getAddress()));
        patientBirthDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonData().getBirthDate()));
        medicalCardColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getMedicalCard().getId())));
    }

    private void loadUsers() {
        Request request = new Request(RequestType.GET_ALL_USERS, null);
        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();

        if (response != null && response.getType() == ResponseType.SUCCESS) {
            allUsers = new Gson().fromJson(response.getMessage(), new TypeToken<List<User>>() {}.getType());
            usersTable.getItems().setAll(allUsers);
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка загрузки пользователей");
        }
    }

    private void loadDoctors() {
        Request request = new Request(RequestType.GET_ALL_DOCTORS, null);
        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();

        if (response != null && response.getType() == ResponseType.SUCCESS) {
            allDoctors = new Gson().fromJson(response.getMessage(), new TypeToken<List<Doctor>>() {}.getType());
            doctorsTable.getItems().setAll(allDoctors);
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка загрузки докторов");
        }
    }

    private void loadPatients() {
        Request request = new Request(RequestType.GET_ALL_PATIENTS, null);
        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();

        if (response != null && response.getType() == ResponseType.SUCCESS) {
            allPatients = new Gson().fromJson(response.getMessage(), new TypeToken<List<Patient>>() {}.getType());
            patientsTable.getItems().setAll(allPatients);
        } else {
            showAlert(response != null ? response.getMessage() : "Ошибка загрузки пациентов");
        }
    }

    private void setupSearchFields() {
        userSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterUsersTable(newValue));
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterUsersTable(userSearchField.getText()));

        doctorSurnameSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterDoctorsTable());
        specializationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterDoctorsTable());

        patientSurnameSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterPatientsTable());
        patientMedicalCardSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterPatientsTable());
    }

    private void filterUsersTable(String username) {
        String selectedRole = roleComboBox.getValue();

        usersTable.getItems().setAll(
                allUsers.stream()
                        .filter(user -> (username == null || username.isEmpty() || user.getUsername().toLowerCase().contains(username.toLowerCase())) &&
                                (selectedRole.equals("Все") || user.getRole().getRoleType().toString().equalsIgnoreCase(selectedRole)))
                        .toList()
        );
    }

    private void filterDoctorsTable() {
        String surname = doctorSurnameSearchField.getText().toLowerCase().trim();
        String specialization = specializationComboBox.getValue();

        doctorsTable.getItems().setAll(
                allDoctors.stream()
                        .filter(doctor -> (surname.isEmpty() || doctor.getPersonData().getSurname().toLowerCase().contains(surname)) &&
                                ("Все".equals(specialization) || specialization == null ||
                                        SpecializationType.valueOf(doctor.getSpecialization().toUpperCase()).getDescription().equals(specialization)))
                        .toList()
        );
    }

    private void filterPatientsTable() {
        String surname = patientSurnameSearchField.getText().toLowerCase().trim();
        String medicalCard = patientMedicalCardSearchField.getText().trim();

        patientsTable.getItems().setAll(
                allPatients.stream()
                        .filter(patient -> (surname.isEmpty() || patient.getPersonData().getSurname().toLowerCase().contains(surname)) &&
                                (medicalCard.isEmpty() || String.valueOf(patient.getMedicalCard().getId()).contains(medicalCard)))
                        .toList()
        );
    }

    private void loadSpecializations() {
        specializationComboBox.getItems().clear();
        specializationComboBox.getItems().add("Все");
        specializationComboBox.getItems().addAll(
                Arrays.stream(SpecializationType.values())
                        .map(SpecializationType::getDescription)
                        .toList()
        );
        specializationComboBox.setValue("Все");
    }

    private void loadRoles() {
        roleComboBox.getItems().addAll("Все", "HEAD_DOCTOR", "DOCTOR", "PATIENT");
        roleComboBox.setValue("Все");
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}