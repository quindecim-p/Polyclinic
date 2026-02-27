package common.enums;

public enum SceneRoute {

    LOGIN("/views/application/login.fxml", "Login"),
    REGISTER("/views/application/register.fxml", "Register"),
    PROFILE("/views/application/profile.fxml", "Profile"),

    PATIENT_MENU("/views/patient/patient_menu.fxml", "Patient Menu"),
    MEDICAL_CARD("/views/patient/medical_card.fxml", "Medical Card"),
    APPOINTMENT("/views/patient/appointment.fxml", "Appointment"),
    SCHEDULES("/views/patient/schedules.fxml", "Schedules"),
    REFERENCES("/views/patient/references.fxml", "References"),
    RECIPES("/views/patient/prescriptions.fxml", "Recipes"),

    DOCTOR_MENU("/views/doctor/doctor_menu.fxml", "Doctor Menu"),
    SCHEDULE("/views/doctor/schedule.fxml", "Schedule"),
    APPOINTMENTS("/views/doctor/appointments.fxml", "Appointments"),
    RECORDS("/views/doctor/records.fxml", "Records"),
    PATIENTS("/views/doctor/patients.fxml", "Patients"),
    HISTORY("/views/doctor/history.fxml", "History"),

    HEAD_DOCTOR_MENU("/views/head_doctor/head_doctor_menu.fxml", "Head Doctor Menu"),
    USERS("/views/head_doctor/users.fxml", "Users"),
    REPORTS("/views/head_doctor/reports.fxml", "Reports"),
    EDIT_SCHEDULES("/views/head_doctor/edit_schedules.fxml", "Edit Schedules"),
    ADD_USERS("/views/head_doctor/add_users.fxml", "Add Users"),;

    private final String fxmlPath;
    private final String title;

    SceneRoute(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }

}