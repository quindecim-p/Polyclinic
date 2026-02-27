package client.controllers.head_doctor;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.validations.RegisterValidation;
import com.google.gson.Gson;
import common.entities.*;
import common.enums.SceneRoute;
import common.enums.errors.ClientError;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.enums.types.RoleType;
import common.enums.types.SpecializationType;
import common.utils.Request;
import common.utils.Response;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AddUsersController {
    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private TextField loginField, surnameField, nameField, patronymicField, emailField, phoneField, addressField, birthdayField, cabinetField, deleteIdField;
    @FXML
    private ComboBox<String> specializationComboBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Label cabinetLabel, specializationLabel, messageLabel;
    @FXML
    private Button addButton, backButton, deleteButton;

    @FXML
    public void initialize() {
        phoneField.setPromptText("+375XXXXXXXXX");
        birthdayField.setPromptText("ДД.ММ.ГГГГ");
        userTypeComboBox.getItems().addAll("Пациент", "Врач");
        specializationComboBox.setVisible(false);
        userTypeComboBox.setOnAction(e -> toggleFields());

        for (SpecializationType type : SpecializationType.values()) {
            specializationComboBox.getItems().add(type.getDescription());
        }
    }

    private void toggleFields() {
        boolean isDoctor = "Врач".equals(userTypeComboBox.getValue());
        cabinetLabel.setVisible(isDoctor);
        cabinetField.setVisible(isDoctor);
        specializationLabel.setVisible(isDoctor);
        specializationComboBox.setVisible(isDoctor);
    }

    @FXML
    private void addButtonPressed() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();
        String surname = surnameField.getText();
        String name = nameField.getText();
        String patronymic = patronymicField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String birthday = birthdayField.getText();

        ClientError validationError = RegisterValidation.validate(
                login, password, repeatPassword, surname, name, patronymic, email, phone, address, birthday);

        if (validationError != null) {
            messageLabel.setText(validationError.getMessage());
            return;
        }

        if ("Пациент".equals(userTypeComboBox.getValue())) {
            PersonData personData = new PersonData(surname, name, patronymic, phone, email, address, birthday);
            MedicalCard medicalCard = new MedicalCard();
            Patient patient = new Patient(personData, medicalCard, new User(login, password, new Role(RoleType.PATIENT)));
            sendRequest(new Request(RequestType.ADD_PATIENT, new Gson().toJson(patient)));
        } else if ("Врач".equals(userTypeComboBox.getValue())) {
            String cabinet = cabinetField.getText();
            String selectedSpecialization = specializationComboBox.getValue();

            if (cabinet.isEmpty() || selectedSpecialization == null) {
                messageLabel.setText(ClientError.EMPTY_DOCTOR_INFO.getMessage());
                return;
            }

            try {
                Integer.parseInt(cabinet);
            } catch (NumberFormatException e) {
                messageLabel.setText(ClientError.INVALID_CABINET.getMessage());
                return;
            }

            SpecializationType specializationType = null;
            for (SpecializationType type : SpecializationType.values()) {
                if (type.getDescription().equals(selectedSpecialization)) {
                    specializationType = type;
                    break;
                }
            }

            if (specializationType == null) {
                messageLabel.setText(ClientError.EMPTY_DOCTOR_INFO.getMessage());
                return;
            }

            Doctor doctor = new Doctor(
                    specializationType.name(),
                    Integer.parseInt(cabinet),
                    new PersonData(surname, name, patronymic, phone, email, address, birthday),
                    new User(login, password, new Role(RoleType.DOCTOR))
            );

            sendRequest(new Request(RequestType.ADD_DOCTOR, new Gson().toJson(doctor)));
        }
    }

    private void sendRequest(Request request) {
        ClientSocket.getInstance().sendRequest(request);
        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            messageLabel.setText(response.getMessage());
            clearFields();
        } else {
            messageLabel.setText(response != null ? response.getMessage() : "Неизвестная ошибка");
        }
    }

    private void clearFields() {
        loginField.clear();
        passwordField.clear();
        repeatPasswordField.clear();
        surnameField.clear();
        nameField.clear();
        patronymicField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        birthdayField.clear();
        cabinetField.clear();
        userTypeComboBox.setValue(null);
        specializationComboBox.setValue(null);
        messageLabel.setText("");
    }

    @FXML
    private void Back_Pressed() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
    }
}