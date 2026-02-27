package client.controllers.application;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import client.validations.LoginValidation;
import common.entities.User;
import com.google.gson.Gson;
import common.enums.errors.ApplicationError;
import common.enums.errors.ClientError;
import common.enums.SceneRoute;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.enums.types.RoleType;
import common.utils.Request;
import common.utils.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerButton;
    @FXML
    private Label messageLabel;

    @FXML
    private void Login_Pressed() throws IOException {
        String login = loginField.getText();
        String password = passwordField.getText();

        messageLabel.setText("");

        ClientError validationError = LoginValidation.validate(login, password);

        if (validationError != null) {
            messageLabel.setText(validationError.getMessage());
            return;
        }

        User user = new User(login, password, null);

        Request request = new Request(RequestType.LOGIN, new Gson().toJson(user));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response != null && response.getType() == ResponseType.SUCCESS) {
            User findUser = new Gson().fromJson(response.getMessage(), User.class);
            Session.getInstance().setCurrentUser(findUser);

            try {
                RoleType roleType = findUser.getRole().getRoleType();

                Stage stage;
                switch (roleType) {
                    case HEAD_DOCTOR:
                        stage = (Stage) loginButton.getScene().getWindow();
                        SceneSwitcher.switchScene(stage, SceneRoute.HEAD_DOCTOR_MENU);
                        break;
                    case DOCTOR:
                        stage = (Stage) loginButton.getScene().getWindow();
                        SceneSwitcher.switchScene(stage, SceneRoute.DOCTOR_MENU);
                        break;
                    case PATIENT:
                        stage = (Stage) loginButton.getScene().getWindow();
                        SceneSwitcher.switchScene(stage, SceneRoute.PATIENT_MENU);
                        break;
                    default:
                        messageLabel.setText(ApplicationError.UNKNOWN_ROLE.getMessage());
                        break;
                }
            } catch (IllegalArgumentException e) {
                messageLabel.setText(ApplicationError.UNKNOWN_ROLE.getMessage());
            }
        } else {
            messageLabel.setText(response != null ? response.getMessage() : ApplicationError.UNEXPECTED_ERROR.getMessage());
        }
    }

    @FXML
    private void Register_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.REGISTER);
    }
}