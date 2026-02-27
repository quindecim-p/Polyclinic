package client.validations;

import client.utils.Validation;
import common.enums.errors.ClientError;

import java.time.LocalDate;

public class AppointmentValidation {
    public static ClientError validate(String symptoms,
                                       String diagnosisName,
                                       String diagnosisDescription,
                                       String prescription,
                                       String reference,
                                       LocalDate referenceValidFrom,
                                       LocalDate referenceValidUntil) {

        if (Validation.isNotEmpty(symptoms)) {
            return ClientError.EMPTY_SYMPTOMS;
        }

        if (Validation.isNotEmpty(diagnosisName)) {
            return ClientError.EMPTY_DIAGNOSIS_NAME;
        }

        if (Validation.isNotEmpty(diagnosisDescription)) {
            return ClientError.EMPTY_DIAGNOSIS_DESCRIPTION;
        }

        if (Validation.isNotEmpty(prescription)) {
            return ClientError.EMPTY_PRESCRIPTION;
        }

        if (Validation.isNotEmpty(reference)) {
            return ClientError.EMPTY_REFERENCE;
        }

        if (referenceValidFrom == null) {
            return ClientError.EMPTY_REFERENCE_FROM;
        }

        if (referenceValidUntil == null) {
            return ClientError.EMPTY_REFERENCE_UNTIL;
        }

        if (referenceValidFrom.isAfter(referenceValidUntil)) {
            return ClientError.INVALID_DATE_RANGE;
        }

        return null;
    }
}
