package common.enums.types;

import java.time.DayOfWeek;

public enum DayOfWeekType {

    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье");

    private final String russianName;

    DayOfWeekType(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }

    public static String fromDayOfWeek(DayOfWeek dayOfWeek) {
        return DayOfWeekType.valueOf(dayOfWeek.name()).getRussianName();
    }

}