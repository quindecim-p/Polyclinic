package client.validations;

import client.utils.Validation;
import common.enums.errors.ClientError;

public class ChangePersonalValidation {
    public static ClientError validate(String surname, String name, String patronymic, String email, String phone, String birthday) {

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

        if (Validation.isValidBirthday(birthday)) {
            return ClientError.INVALID_BIRTHDAY;
        }

        if (Validation.isValidYear(birthday)) {
            return ClientError.INVALID_YEAR;
        }

        return null;
    }
}