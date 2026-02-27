package common.enums.errors;

public enum ServerError {
    USERNAME_EXISTS("Логин уже существует"),
    PHONE_EXISTS("Номер телефона уже существует"),
    EMAIL_EXISTS("Почта уже существует"),
    INVALID_CREDENTIALS("Неверный логин или пароль"),
    INVALID_DATA("Неверные данные для проверки"),
    UPDATE_FAILED("Ошибка при обновлении данных"),
    MEDICAL_CARD_NOT_FOUND("Мед. карта не найдена"),
    USERS_NOT_FOUND("Польователи не найдены"),
    DOCTORS_NOT_FOUND("Врачи не найдены"),
    PATIENTS_NOT_FOUND("Пациенты не найдены"),
    APPOINTMENTS_NOT_FOUND("Посещения не найдены"),
    USER_NOT_FOUND("Пользователь не найден"),
    DOCTOR_NOT_FOUND("Доктор не найден"),
    WORKING_SCHEDULE_NOT_FOUND("Расписание не найдено"),
    WORK_DAY_NOT_FOUND("Рабочий день не найден"),
    PATIENT_ALREADY_BOOKED("У вас уже есть запись к этому врачу"),
    DELETE_YOURSELF("Себя удалить нельзя"),
    INTERNAL_SERVER_ERROR("Ошибка на сервере");

    private final String message;

    ServerError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}