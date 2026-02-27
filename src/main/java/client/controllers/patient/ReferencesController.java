package client.controllers.patient;

import client.utils.ClientSocket;
import client.utils.SceneSwitcher;
import client.utils.Session;
import com.google.gson.GsonBuilder;
import common.entities.Reference;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReferencesController {

    @FXML
    private TableView<Reference> referencesTable;
    @FXML
    private TableColumn<Reference, String> detailsColumn;
    @FXML
    private TableColumn<Reference, String> validFromColumn;
    @FXML
    private TableColumn<Reference, String> validUntilColumn;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField searchField;

    private List<Reference> references;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadReferencesData();
    }

    private void setupTableColumns() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        detailsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDetails()));
        validFromColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(dateFormat.format(data.getValue().getValidFrom())));
        validUntilColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(dateFormat.format(data.getValue().getValidUntil())));
    }

    private void loadReferencesData() {
        int patientId = Session.getInstance().getCurrentUserId();
        Request request = new Request(RequestType.GET_REFERENCES, String.valueOf(patientId));
        ClientSocket.getInstance().sendRequest(request);

        Response response = ClientSocket.getInstance().receiveResponse();
        if (response == null || response.getType() != ResponseType.SUCCESS) {
            showAlert(ApplicationError.UNEXPECTED_ERROR.getMessage());
            return;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        references = gson.fromJson(response.getMessage(), new TypeToken<List<Reference>>() {}.getType());
        updateTable(references);
    }

    private void updateTable(List<Reference> references) {
        ObservableList<Reference> observableList = FXCollections.observableArrayList(references);
        referencesTable.setItems(observableList);
    }

    @FXML
    private void applyFilters() {
        Date startDate = startDatePicker.getValue() == null ? null : java.sql.Date.valueOf(startDatePicker.getValue());
        Date endDate = endDatePicker.getValue() == null ? null : java.sql.Date.valueOf(endDatePicker.getValue());
        String searchKeyword = searchField.getText().toLowerCase();

        List<Reference> filteredReferences = references.stream()
                .filter(ref -> (startDate == null || !ref.getValidFrom().before(startDate)) &&
                        (endDate == null || !ref.getValidUntil().after(endDate)) &&
                        (searchKeyword.isEmpty() || ref.getDetails().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());

        updateTable(filteredReferences);
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) referencesTable.getScene().getWindow();
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