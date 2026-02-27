package client.controllers.application;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.validations.RegisterValidation;
import com.google.gson.Gson;
import common.enums.errors.ApplicationError;
import common.enums.errors.ClientError;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.enums.types.RoleType;
import common.utils.Request;
import common.utils.Response;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import common.entities.*;

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField patronymicField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField birthdayField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Hyperlink loginButton;

    @FXML
    public void initialize() {
        phoneField.setPromptText("+375XXXXXXXXX");
        birthdayField.setPromptText("ДД.ММ.ГГГГ");
    }

    @FXML
    private void Register_Pressed() throws IOException {
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

        messageLabel.setText("");

        ClientError validationError = RegisterValidation.validate(login, password, repeatPassword, surname, name, patronymic, email, phone, address, birthday);

        if (validationError != null) {
            messageLabel.setText(validationError.getMessage());
            return;
        }

        Role role = new Role(RoleType.PATIENT);
        User user = new User(login, password, role);

        PersonData personData = new PersonData(surname, name, patronymic, phone, email, address, birthday);
        MedicalCard medicalCard = new MedicalCard();

        Patient patient = new Patient(personData, medicalCard, user);

        Request request = new Request(RequestType.REGISTER, new Gson().toJson(patient));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            SceneSwitcher.switchScene(stage, SceneRoute.LOGIN);
        } else {
            messageLabel.setText(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
        }
    }

    @FXML
    public void Login_Pressed() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.LOGIN);
    }
}