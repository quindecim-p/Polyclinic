package common.enums;

public enum SuccessMessage {

    PATIENT_REGISTERED("Пациент успешно зарегистрирован"),
    DOCTOR_REGISTERED("Врач успешно зарегистрирован"),
    DATA_RECEIVED("Данные успешно получены"),
    APPOINTMENT_CREATE("Запись успешно создана"),
    APPOINTMENT_CANCEL("Запись успешно отменена"),
    APPOINTMENT_COMPLETED("Прием успешно завершен"),
    WORK_DAY_UPDATED("Рабочий день успешно обновлен"),
    WORK_DAY_ADDED("Рабочий день успешно добавлен"),
    WORK_DAY_DELETED("Рабочий день успешно удален"),
    USER_DELETED("Пользователь успешно удален"),
    DATA_UPDATED("Данные успешно обновлены");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}