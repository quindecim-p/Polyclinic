package client.controllers.head_doctor;

import client.utils.SceneSwitcher;
import common.enums.SceneRoute;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HeadDoctorMenuController {

    @FXML
    private Button profileButton;

    @FXML
    private Button appointmentsButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button editSchedulesButton;

    @FXML
    private Button addUsersButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void Profile_Pressed() throws IOException {
        Stage stage = (Stage) profileButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PROFILE);
    }

    @FXML
    private void Appointments_Pressed() throws IOException {
        Stage stage = (Stage) appointmentsButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.APPOINTMENTS);
    }

    @FXML
    private void Users_Pressed() throws IOException {
        Stage stage = (Stage) usersButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.USERS);
    }

    @FXML
    private void Reports_Pressed() throws IOException {
        Stage stage = (Stage) reportsButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.REPORTS);
    }

    @FXML
    private void EditSchedules_Pressed() throws IOException {
        Stage stage = (Stage) editSchedulesButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.EDIT_SCHEDULES);
    }

    @FXML
    private void AddUsers_Pressed() throws IOException {
        Stage stage = (Stage) addUsersButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.ADD_USERS);
    }

    @FXML
    private void Logout_Pressed() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.LOGIN);
    }

}