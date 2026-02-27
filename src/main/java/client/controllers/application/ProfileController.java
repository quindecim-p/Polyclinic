package client.controllers.application;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import client.validations.ChangeCredentialsValidation;
import client.validations.ChangePersonalValidation;
import com.google.gson.Gson;
import common.entities.User;
import common.enums.errors.ApplicationError;
import common.enums.errors.ClientError;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.enums.types.RoleType;
import common.utils.Request;
import common.utils.Response;
import common.entities.PersonData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    @FXML
    private TextField surnameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField patronymicField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField birthdayField;

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private Button editPersonalButton;
    @FXML
    private Button savePersonalButton;

    @FXML
    private Button editCredentialsButton;
    @FXML
    private Button saveCredentialsButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        loadUserData();
    }

    private void loadUserData() {
        int userId = Session.getInstance().getCurrentUserId();

        Request request = new Request(RequestType.GET_PROFILE, String.valueOf(userId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            PersonData personData = new Gson().fromJson(response.getMessage(), PersonData.class);
            fillPersonalFields(personData);
        } else {
            messageLabel.setText(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
        }
    }

    private void fillPersonalFields(PersonData personData) {
        surnameField.setText(personData.getSurname());
        nameField.setText(personData.getName());
        patronymicField.setText(personData.getPatronymic());
        phoneField.setText(personData.getPhone());
        emailField.setText(personData.getEmail());
        addressField.setText(personData.getAddress());
        birthdayField.setText(personData.getBirthDate());
    }

    @FXML
    private void SavePersonal_Pressed() {
        String surname = surnameField.getText();
        String name = nameField.getText();
        String patronymic = patronymicField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String birthday = birthdayField.getText();

        messageLabel.setText("");

        ClientError validationError = ChangePersonalValidation.validate(surname, name, patronymic, email, phone, birthday);

        if (validationError != null) {
            messageLabel.setText(validationError.getMessage());
            return;
        }

        PersonData personData = new PersonData(surname, name, patronymic, phone, email, address, birthday);

        Request request = new Request(RequestType.UPDATE_PERSONAL, new Gson().toJson(personData));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            messageLabel.setText(response.getMessage());
            togglePersonalFields(false);
        } else {
            messageLabel.setText(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
        }
    }

    @FXML
    private void SaveCredentials_Pressed() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        messageLabel.setText("");

        ClientError validationError = ChangeCredentialsValidation.validate(login, password, repeatPassword);

        if (validationError != null) {
            messageLabel.setText(validationError.getMessage());
            return;
        }

        User user = new User(login, password, Session.getInstance().getCurrentUser().getRole());
        user.setId(Session.getInstance().getCurrentUserId());

        Request request = new Request(RequestType.UPDATE_CREDENTIALS, new Gson().toJson(user));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            messageLabel.setText(response.getMessage());
            toggleCredentialsFields(false);
        } else {
            messageLabel.setText(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
        }
    }

    @FXML
    private void EnableEditingPersonal_Pressed() {
        togglePersonalFields(true);
    }

    private void togglePersonalFields(boolean enable) {
        surnameField.setDisable(!enable);
        nameField.setDisable(!enable);
        patronymicField.setDisable(!enable);
        phoneField.setDisable(!enable);
        emailField.setDisable(!enable);
        addressField.setDisable(!enable);
        birthdayField.setDisable(!enable);

        editPersonalButton.setDisable(enable);
        savePersonalButton.setDisable(!enable);
    }

    @FXML
    private void EnableEditingCredentials_Pressed() {
        toggleCredentialsFields(true);
    }

    private void toggleCredentialsFields(boolean enable) {
        loginField.setText("");
        passwordField.setText("");
        repeatPasswordField.setText("");

        loginField.setDisable(!enable);
        passwordField.setDisable(!enable);
        repeatPasswordField.setDisable(!enable);

        editCredentialsButton.setDisable(enable);
        saveCredentialsButton.setDisable(!enable);
    }

    @FXML
    private void Back_Pressed() throws IOException {
        RoleType roleType = Session.getInstance().getCurrentUser().getRole().getRoleType();

        Stage stage;
        switch (roleType) {
            case HEAD_DOCTOR:
                stage = (Stage) backButton.getScene().getWindow();
                SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
                break;
            case DOCTOR:
                stage = (Stage) backButton.getScene().getWindow();
                SceneSwitcher.switchScene(stage, SceneRoute.DOCTOR_MENU);
                break;
            case PATIENT:
                stage = (Stage) backButton.getScene().getWindow();
                SceneSwitcher.switchScene(stage, SceneRoute.PATIENT_MENU);
                break;
            default:
                messageLabel.setText(ApplicationError.UNKNOWN_ROLE.getMessage());
                break;
        }
    }
}