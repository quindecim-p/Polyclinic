package server.dto;

import java.util.List;

public class DoctorDTO {
    private int id;
    private String surname;
    private String specialization;
    private int officeNumber;
    private int scheduleId;
    private List<WorkDayDTO> workDays;

    public DoctorDTO() {}

    public DoctorDTO(int id, String surname, String specialization, int officeNumber,
                     int scheduleId, List<WorkDayDTO> workDays) {
        this.id = id;
        this.surname = surname;
        this.specialization = specialization;
        this.officeNumber = officeNumber;
        this.scheduleId = scheduleId;
        this.workDays = workDays;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getSpecialization() { return specialization; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getOfficeNumber() { return officeNumber; }

    public void setOfficeNumber(int officeNumber) { this.officeNumber = officeNumber; }

    public int getScheduleId() { return scheduleId; }

    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public List<WorkDayDTO> getWorkDays() { return workDays; }

    public void setWorkDays(List<WorkDayDTO> workDays) { this.workDays = workDays; }

}