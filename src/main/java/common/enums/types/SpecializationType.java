package common.enums.types;

public enum SpecializationType {

    THERAPIST("Терапевт"),
    PEDIATRICIAN("Педиатр"),
    CARDIOLOGIST("Кардиолог"),
    NEUROLOGIST("Невролог"),
    SURGEON("Хирург"),
    DERMATOLOGIST("Дерматолог"),
    GYNECOLOGIST("Гинеколог"),
    ONCOLOGIST("Онколог"),
    PSYCHIATRIST("Психиатр"),
    DENTIST("Стоматолог"),
    ENDOCRINOLOGIST("Эндокринолог"),
    OTOLARYNGOLOGIST("ЛОР (Отоларинголог)"),
    OPHTHALMOLOGIST("Офтальмолог"),
    UROLOGIST("Уролог"),
    ORTHOPEDIST("Ортопед");

    private final String description;

    SpecializationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}