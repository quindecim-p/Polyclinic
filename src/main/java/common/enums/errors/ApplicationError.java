package common.enums.errors;

public enum ApplicationError {
    UNKNOWN_ROLE("Неизвестная роль пользователя"),
    UNKNOWN_REQUEST("Неизвестный тип запроса"),
    UNEXPECTED_ERROR("Неожиданная ошибка");

    private final String message;

    ApplicationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}