package client.validations;

import client.utils.Validation;
import common.enums.errors.ClientError;

public class ChangeCredentialsValidation {
    public static ClientError validate(String login, String password, String repeatPassword) {

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

        return null;
    }
}