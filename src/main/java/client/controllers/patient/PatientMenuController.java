package client.controllers.patient;

import client.utils.SceneSwitcher;
import common.enums.SceneRoute;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class PatientMenuController {

    @FXML
    private Button profileButton;

    @FXML
    private Button medicalCardButton;

    @FXML
    private Button appointmentButton;

    @FXML
    private Button schedulesButton;

    @FXML
    private Button referencesButton;

    @FXML
    private Button recipesButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void Profile_Pressed() throws IOException {
        Stage stage = (Stage) profileButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PROFILE);
    }

    @FXML
    private void MedicalCard_Pressed() throws IOException {
        Stage stage = (Stage) medicalCardButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.MEDICAL_CARD);
    }

    @FXML
    private void Appointment_Pressed() throws IOException {
        Stage stage = (Stage) appointmentButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.APPOINTMENT);
    }

    @FXML
    private void Schedules_Pressed() throws IOException {
        Stage stage = (Stage) schedulesButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.SCHEDULES);
    }

    @FXML
    private void References_Pressed() throws IOException {
        Stage stage = (Stage) referencesButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.REFERENCES);
    }

    @FXML
    private void Recipes_Pressed() throws IOException {
        Stage stage = (Stage) recipesButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.RECIPES);
    }

    @FXML
    private void Logout_Pressed() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.LOGIN);
    }
}