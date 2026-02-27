package client.controllers.doctor;

import client.utils.SceneSwitcher;
import common.enums.SceneRoute;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorMenuController {

    @FXML
    private Button profileButton;

    @FXML
    private Button scheduleButton;

    @FXML
    private Button appointmentsButton;

    @FXML
    private Button recordsButton;

    @FXML
    private Button patientsButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void Profile_Pressed() throws IOException {
        Stage stage = (Stage) profileButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PROFILE);
    }

    @FXML
    private void Schedule_Pressed() throws IOException {
        Stage stage = (Stage) scheduleButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.SCHEDULE);
    }

    @FXML
    private void Appointments_Pressed() throws IOException {
        Stage stage = (Stage) appointmentsButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.APPOINTMENTS);
    }

    @FXML
    private void Records_Pressed() throws IOException {
        Stage stage = (Stage) recordsButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.RECORDS);
    }

    @FXML
    private void Patients_Pressed() throws IOException {
        Stage stage = (Stage) patientsButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PATIENTS);
    }

    @FXML
    private void History_Pressed() throws IOException {
        Stage stage = (Stage) historyButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.HISTORY);
    }

    @FXML
    private void Logout_Pressed() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.LOGIN);
    }
}