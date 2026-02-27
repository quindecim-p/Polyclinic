package client.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validation {

    public static boolean isNotEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isPasswordStrong(String password) {
        return password.length() < 8;
    }

    public static boolean isValidName(String name) {
        return !name.matches("[a-zA-Zа-яА-ЯёЁ]+") || name.length() <= 2;
    }

    public static boolean isValidEmail(String email) {
        return !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return !phone.matches("\\+375\\d{9}");
    }

    public static boolean isValidBirthday(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            LocalDate.parse(birthday, formatter);
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    public static boolean isValidYear(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            LocalDate birthDate = LocalDate.parse(birthday, formatter);
            int year = birthDate.getYear();
            int currentYear = LocalDate.now().getYear();
            return year < 1900 || year > currentYear;
        } catch (DateTimeParseException e) {
            return true;
        }
    }
}