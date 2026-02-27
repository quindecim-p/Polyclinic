package server.dto;

import common.entities.Doctor;
import common.entities.WorkDay;
import common.enums.types.SpecializationType;
import server.services.WorkingScheduleService;
import server.services.WorkDayService;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorDTOMapper {

    private final WorkingScheduleService workingScheduleService;
    private final WorkDayService workDayService;

    public DoctorDTOMapper(WorkingScheduleService scheduleService, WorkDayService workDayService) {
        this.workingScheduleService = scheduleService;
        this.workDayService = workDayService;
    }

    public DoctorDTO mapToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setSurname(doctor.getPersonData().getSurname());

        dto.setSpecialization(
                SpecializationType.valueOf(doctor.getSpecialization().toUpperCase()).getDescription()
        );

        dto.setOfficeNumber(doctor.getOfficeNumber());

        var schedule = workingScheduleService.findByDoctorId(doctor.getId());
        if (schedule != null) {
            dto.setScheduleId(schedule.getId());
            List<WorkDay> workDays = workDayService.findByScheduleId(schedule.getId());
            dto.setWorkDays(workDays.stream()
                    .map(this::mapWorkDayToDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private WorkDayDTO mapWorkDayToDTO(WorkDay workDay) {
        return new WorkDayDTO(
                workDay.getDay(),
                workDay.getStartTime(),
                workDay.getEndTime()
        );
    }

    public List<DoctorDTO> mapToDTOs(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::mapToDTO)
                .toList();
    }
}