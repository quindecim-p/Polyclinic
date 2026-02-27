package client.validations;

import client.utils.Validation;
import common.enums.errors.ClientError;

public class RegisterValidation {
    public static ClientError validate(String login, String password, String repeatPassword, String surname, String name, String patronymic, String email, String phone, String address, String birthday) {

        if (Validation.isNotEmpty(login)) {
            return ClientError.EMPTY_LOGIN;
        }

        if (Validation.isNotEmpty(password)) {
            return ClientError.EMPTY_PASSWORD;
        }

        if (Validation.isNotEmpty(repeatPassword)) {
            return ClientError.EMPTY_REPEAT_PASSWORD;
        }

        if (!password.equals(repeatPassword)) {
            return ClientError.PASSWORDS_DO_NOT_MATCH;
        }

        if (Validation.isPasswordStrong(password)) {
            return ClientError.WEAK_PASSWORD;
        }

        if (Validation.isNotEmpty(surname) || Validation.isValidName(surname)) {
            return ClientError.INVALID_SURNAME;
        }

        if (Validation.isNotEmpty(name) || Validation.isValidName(name)) {
            return ClientError.INVALID_NAME;
        }

        if (Validation.isNotEmpty(patronymic) || Validation.isValidName(patronymic)) {
            return ClientError.INVALID_PATRONYMIC;
        }

        if (Validation.isValidEmail(email)) {
            return ClientError.INVALID_EMAIL;
        }

        if (Validation.isValidPhone(phone)) {
            return ClientError.INVALID_PHONE;
        }

        if (Validation.isNotEmpty(address)) {
            return ClientError.EMPTY_ADDRESS;
        }

        if (Validation.isValidBirthday(birthday)) {
            return ClientError.INVALID_BIRTHDAY;
        }

        if (Validation.isValidYear(birthday)) {
            return ClientError.INVALID_YEAR;
        }

        return null;
    }
}