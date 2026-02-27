package common.enums.errors;

public enum ClientError {
    EMPTY_LOGIN("Логин не может быть пустым"),
    EMPTY_PASSWORD("Пароль не может быть пустым"),
    EMPTY_REPEAT_PASSWORD("Повтор пароля не может быть пустым"),
    EMPTY_SYMPTOMS("Введите симптомы"),
    EMPTY_DIAGNOSIS_NAME("Введите название диагноза"),
    EMPTY_DIAGNOSIS_DESCRIPTION("Введите описание диагноза"),
    EMPTY_PRESCRIPTION("Введите рецепт"),
    EMPTY_REFERENCE("Введите справку"),
    EMPTY_REFERENCE_FROM("Введите дату начала действия справки"),
    EMPTY_REFERENCE_UNTIL("Введите дату конца действия справки"),
    EMPTY_DOCTOR_INFO("Поля кабинет и специализация обязательны для заполнения"),
    EMPTY_ADDRESS("Адресс не может быть пустым"),
    INVALID_SURNAME("Введите фамилию корректно"),
    INVALID_NAME("Введите имя корректно"),
    INVALID_PATRONYMIC("Введите отчество корректно"),
    INVALID_EMAIL("Некорректный e-mail"),
    INVALID_PHONE("Некорректный номер телефона (Формат: +375XXXXXXXXX)"),
    INVALID_BIRTHDAY("Некорректная дата рождения (Формат: ДД.ММ.ГГГГ)"),
    INVALID_YEAR("Год рождения должен быть в пределах от 1900 до текущего года"),
    INVALID_DATE_RANGE("Некорректный диапазон дат"),
    INVALID_CABINET("Кабинет должен быть числом"),
    CHOOSE_PATIENT("Выберите пациента для проведения приема"),
    CHOOSE_APPOINTMENT("Выберите посещение для отмены"),
    CHOOSE_APPOINTMENT_PARAMETERS("Выберите врача, дату и время и укажите симптомы"),
    CHOOSE_SCHEDULE_PARAMETRS("Выберите врача и день недели"),
    DOCTOR_NOT_WORK("На выбранный день врач не работает"),
    PASSWORDS_DO_NOT_MATCH("Пароли не совпадают"),
    WEAK_PASSWORD("Пароль должен быть длиной не менее 8 символов");

    private final String message;

    ClientError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}