package client.validations;

import client.utils.Validation;
import common.enums.errors.ClientError;

public class LoginValidation {
    public static ClientError validate(String login, String password) {

        if (Validation.isNotEmpty(login)) {
            return ClientError.EMPTY_LOGIN;
        }

        if (Validation.isNotEmpty(password)) {
            return ClientError.EMPTY_PASSWORD;
        }

        return null;
    }
}