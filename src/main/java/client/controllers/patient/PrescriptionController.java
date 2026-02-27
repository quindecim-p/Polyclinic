package client.controllers.patient;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.GsonBuilder;
import common.entities.Prescription;
import common.enums.SceneRoute;
import common.enums.errors.ApplicationError;
import common.enums.types.RequestType;
import common.enums.types.ResponseType;
import common.utils.LocalDateTimeAdapter;
import common.utils.Request;
import common.utils.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionController {

    @FXML
    private TableView<Prescription> prescriptionsTable;
    @FXML
    private TableColumn<Prescription, String> instructionsColumn;
    @FXML
    private TextField searchField;

    private List<Prescription> prescriptions;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadPrescriptionsData();
    }

    private void setupTableColumns() {
        instructionsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getInstructions()));
    }

    private void loadPrescriptionsData() {
        int patientId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_PRESCRIPTIONS, String.valueOf(patientId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(ApplicationError.UNEXPECTED_ERROR.getMessage());
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        prescriptions = gson.fromJson(response.getMessage(), new TypeToken<List<Prescription>>() {}.getType());
        updateTable(prescriptions);
    }

    private void updateTable(List<Prescription> prescriptions) {
        ObservableList<Prescription> observableList = FXCollections.observableArrayList(prescriptions);
        prescriptionsTable.setItems(observableList);
    }

    @FXML
    private void applyFilters() {
        String searchKeyword = searchField.getText().toLowerCase();

        List<Prescription> filteredPrescriptions = prescriptions.stream()
                .filter(prescription -> searchKeyword.isEmpty() || prescription.getInstructions().toLowerCase().contains(searchKeyword))
                .collect(Collectors.toList());

        updateTable(filteredPrescriptions);
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) prescriptionsTable.getScene().getWindow();
        SceneSwitcher.switchScene(stage, SceneRoute.PATIENT_MENU);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}