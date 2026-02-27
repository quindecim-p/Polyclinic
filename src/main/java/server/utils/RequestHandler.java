package server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.utils.*;
import server.dto.*;
import common.entities.*;
import common.enums.SuccessMessage;
import common.enums.types.ResponseType;
import common.enums.errors.ServerError;
import server.services.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class RequestHandler {

    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MedicalCardService medicalCardService;
    private final AppointmentService appointmentService;
    private final DiagnosisService diagnosisService;
    private final PrescriptionService prescriptionService;
    private final ReferenceService referenceService;
    private final WorkDayService workDayService;
    private final WorkingScheduleService workingScheduleService;
    private final Gson gson;

    public RequestHandler(UserService userService, PatientService patientService, DoctorService doctorService,
                          MedicalCardService medicalCardService, AppointmentService appointmentService,
                          DiagnosisService diagnosisService, PrescriptionService prescriptionService,
                          ReferenceService referenceService, WorkDayService workDayService,
                          WorkingScheduleService workingScheduleService) {
        this.userService = userService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.medicalCardService = medicalCardService;
        this.appointmentService = appointmentService;
        this.diagnosisService = diagnosisService;
        this.prescriptionService = prescriptionService;
        this.referenceService = referenceService;
        this.workDayService = workDayService;
        this.workingScheduleService = workingScheduleService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DayOfWeek.class, new DayOfWeekAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }

    public Response handleRequest(Request request) {
        try {
            return switch (request.getType()) {
                case REGISTER -> handleRegister(request);
                case LOGIN -> handleLogin(request);
                case GET_PROFILE -> handleProfile(request);
                case UPDATE_PERSONAL -> handleUpdateProfile(request);
                case UPDATE_CREDENTIALS -> handleUpdateCredentials(request);

                case GET_MEDICAL_CARD -> handleMedicalCard(request);
                case CANCEL_APPOINTMENT -> handleCancelAppointment(request);
                case GET_REFERENCES -> handleReferences(request);
                case GET_PRESCRIPTIONS -> handlePrescriptions(request);
                case GET_SCHEDULES -> handleSchedules(request);
                case GET_AVAILABLE_TIMES -> handleAvailableTimes(request);
                case GET_MEDICAL_CARD_ID -> handleMedicalCardId(request);
                case ADD_APPOINTMENT -> handleAppointmentCreate(request);

                case GET_APPOINTMENT_HISTORY -> handleAppointmentHistory(request);
                case GET_APPOINTMENT_RECORDS -> handleAppointmentRecords(request);
                case GET_PATIENTS -> handlePatients(request);
                case GET_WORKING_SCHEDULE -> handleWorkingSchedule(request);
                case GET_TODAY_APPOINTMENTS -> handleTodayAppointments(request);
                case UPDATE_APPOINTMENT -> handleUpdateAppointment(request);

                case GET_ALL_USERS -> handleAllUsers(request);
                case GET_ALL_DOCTORS -> handleAllDoctors(request);
                case GET_ALL_PATIENTS -> handleAllPatients(request);
                case GET_ALL_APPOINTMENTS -> handleAllAppointments(request);
                case UPDATE_WORK_DAY -> handleUpdateWorkDay(request);
                case ADD_WORK_DAY -> handleAddWorkDay(request);
                case DELETE_WORK_DAY -> handleDeleteWorkDay(request);
                case ADD_DOCTOR -> handleAddDoctor(request);
                case ADD_PATIENT -> handleAddPatient(request);
            };
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }



    private Response handleRegister(Request request) {
        try {
            Patient patient = gson.fromJson(request.getMessage(), Patient.class);
            patientService.saveEntity(patient);
            return new Response(ResponseType.SUCCESS, SuccessMessage.PATIENT_REGISTERED.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.USERNAME_EXISTS.getMessage());
        }
    }

    private Response handleLogin(Request request) {
        try {
            User user = gson.fromJson(request.getMessage(), User.class);
            String findUser = gson.toJson(userService.login(user));
            return new Response(ResponseType.SUCCESS, findUser);
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleProfile(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());
            PersonData personData = userService.getProfile(userId);
            return new Response(ResponseType.SUCCESS, gson.toJson(personData));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleUpdateProfile(Request request) {
        try {
            PersonData personData = gson.fromJson(request.getMessage(), PersonData.class);
            userService.updateProfile(personData);
            return new Response(ResponseType.SUCCESS, SuccessMessage.DATA_UPDATED.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.UPDATE_FAILED.getMessage());
        }
    }

    private Response handleUpdateCredentials(Request request) {
        try {
            User user = gson.fromJson(request.getMessage(), User.class);
            userService.updateEntity(user);
            return new Response(ResponseType.SUCCESS, SuccessMessage.DATA_UPDATED.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.UPDATE_FAILED.getMessage());
        }
    }



    private Response handleMedicalCard(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());
            MedicalCard medicalCard = medicalCardService.findByUserId(userId);

            if (medicalCard == null) {
                return new Response(ResponseType.ERROR, ServerError.MEDICAL_CARD_NOT_FOUND.getMessage());
            }

            List<Appointment> appointments = appointmentService.findByMedicalCardId(medicalCard.getId());

            AppointmentDTOMapper appointmentDTOMapper = new AppointmentDTOMapper(diagnosisService, prescriptionService, referenceService, doctorService, patientService);

            List<AppointmentDTO> appointmentDTOs = appointmentDTOMapper.mapToDTOs(appointments);

            return new Response(ResponseType.SUCCESS, gson.toJson(appointmentDTOs));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleCancelAppointment(Request request) {
        try {
            int appointmentId = Integer.parseInt(request.getMessage());
            appointmentService.deleteById(appointmentId);
            return new Response(ResponseType.SUCCESS, SuccessMessage.APPOINTMENT_CANCEL.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleReferences(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());
            List<Reference> references = referenceService.findByUserId(userId);
            return new Response(ResponseType.SUCCESS, gson.toJson(references));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handlePrescriptions(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());
            List<Prescription> prescriptions = prescriptionService.findByUserId(userId);
            return new Response(ResponseType.SUCCESS, gson.toJson(prescriptions));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleSchedules(Request request) {
        try {
            List<Doctor> doctors = doctorService.findAllEntities();
            if (doctors == null || doctors.isEmpty()) {
                return new Response(ResponseType.ERROR, ServerError.DOCTORS_NOT_FOUND.getMessage());
            }
            DoctorDTOMapper doctorDTOMapper = new DoctorDTOMapper(workingScheduleService, workDayService);
            List<DoctorDTO> doctorDTOs = doctorDTOMapper.mapToDTOs(doctors);
            return new Response(ResponseType.SUCCESS, gson.toJson(doctorDTOs));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAvailableTimes(Request request) {
        try {
            String[] data = request.getMessage().split(";");
            if (data.length != 2) {
                return new Response(ResponseType.ERROR, ServerError.INVALID_DATA.getMessage());
            }

            int doctorId = Integer.parseInt(data[0]);
            LocalDateTime appointmentDateTime = LocalDateTime.parse(data[1]);

            boolean isTimeOccupied = appointmentService.isTimeOccupied(doctorId, appointmentDateTime);
            return new Response(ResponseType.SUCCESS, String.valueOf(isTimeOccupied));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleMedicalCardId(Request request) {
        try {
            int userId = Integer.parseInt(request.getMessage());
            MedicalCard medicalCard = medicalCardService.findByUserId(userId);

            if (medicalCard == null) {
                return new Response(ResponseType.ERROR, ServerError.MEDICAL_CARD_NOT_FOUND.getMessage());
            }

            return new Response(ResponseType.SUCCESS, gson.toJson(medicalCard));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAppointmentCreate(Request request) {
        try {
            Appointment appointment = gson.fromJson(request.getMessage(), Appointment.class);

            boolean isAlreadyBooked = appointmentService.isAlreadyBooked(
                    appointment.getDoctor().getId(),
                    appointment.getMedicalCard().getId()
            );

            if (isAlreadyBooked) {
                return new Response(ResponseType.ERROR, ServerError.PATIENT_ALREADY_BOOKED.getMessage());
            }

            System.out.println(appointment.getAppointmentDate());
            System.out.println(appointment.getDoctor().getId());
            System.out.println(appointment.getMedicalCard().getId());
            System.out.println(appointment.getStatus());

            appointmentService.saveEntity(appointment);

            return new Response(ResponseType.SUCCESS, SuccessMessage.APPOINTMENT_CREATE.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }



    private Response handleAppointmentHistory(Request request) {
        try {
            int doctorId = Integer.parseInt(request.getMessage());
            List<Appointment> appointments = appointmentService.findByDoctorIdHistory(doctorId);
            AppointmentDTOMapper appointmentDTOMapper = new AppointmentDTOMapper(
                    diagnosisService, prescriptionService, referenceService, doctorService, patientService
            );
            List<AppointmentDTO> appointmentDTOs = appointmentDTOMapper.mapToDTOs(appointments);
            return new Response(ResponseType.SUCCESS, gson.toJson(appointmentDTOs));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAppointmentRecords(Request request) {
        try {
            int doctorId = Integer.parseInt(request.getMessage());
            List<Appointment> appointments = appointmentService.findByDoctorIdRecords(doctorId);
            AppointmentDTOMapper appointmentDTOMapper = new AppointmentDTOMapper(
                    diagnosisService, prescriptionService, referenceService, doctorService, patientService
            );
            List<AppointmentDTO> appointmentDTOs = appointmentDTOMapper.mapToDTOs(appointments);
            return new Response(ResponseType.SUCCESS, gson.toJson(appointmentDTOs));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handlePatients(Request request) {
        try {
            int doctorId = Integer.parseInt(request.getMessage());
            List<Patient> patients = patientService.findByDoctorId(doctorId);
            if (patients == null || patients.isEmpty()) {
                return new Response(ResponseType.ERROR, ServerError.PATIENTS_NOT_FOUND.getMessage());
            }
            return new Response(ResponseType.SUCCESS, gson.toJson(patients));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleWorkingSchedule(Request request) {
        try {
            int doctorId = Integer.parseInt(request.getMessage());
            WorkingSchedule workingSchedule = workingScheduleService.findByDoctorId(doctorId);
            if (workingSchedule == null) {
                return new Response(ResponseType.ERROR, ServerError.WORKING_SCHEDULE_NOT_FOUND.getMessage());
            }
            List<WorkDay> workDays = workDayService.findByScheduleId(workingSchedule.getId());
            return new Response(ResponseType.SUCCESS, gson.toJson(workDays));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleTodayAppointments(Request request) {
        try {
            int doctorId = Integer.parseInt(request.getMessage());
            List<Appointment> appointments = appointmentService.findByDoctorIdToday(doctorId);
            AppointmentDTOMapper appointmentDTOMapper = new AppointmentDTOMapper(
                    diagnosisService, prescriptionService, referenceService, doctorService, patientService
            );
            List<AppointmentDTO> appointmentDTOs = appointmentDTOMapper.mapToDTOs(appointments);
            return new Response(ResponseType.SUCCESS, gson.toJson(appointmentDTOs));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleUpdateAppointment(Request request) {
        try {
            AppointmentDTO appointmentDTO = gson.fromJson(request.getMessage(), AppointmentDTO.class);

            AppointmentDTOMapper appointmentDTOMapper = new AppointmentDTOMapper(
                    diagnosisService, prescriptionService, referenceService, doctorService, patientService
            );

            Appointment appointment = appointmentDTOMapper.mapToAppointment(appointmentDTO);
            Diagnosis diagnosis = appointmentDTOMapper.mapToDiagnosis(appointment, appointmentDTO);
            Prescription prescription = appointmentDTOMapper.mapToPrescription(appointment, appointmentDTO);
            Reference reference = appointmentDTOMapper.mapToReference(appointment, appointmentDTO);

            appointmentService.updateEntity(appointment);
            diagnosisService.saveEntity(diagnosis);
            prescriptionService.saveEntity(prescription);
            referenceService.saveEntity(reference);

            return new Response(ResponseType.SUCCESS, gson.toJson(SuccessMessage.APPOINTMENT_COMPLETED.getMessage()));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }



    private Response handleAllUsers(Request request) {
        try {
            List<User> users = userService.findAllEntities();
            if (users == null || users.isEmpty()) {
                return new Response(ResponseType.ERROR, ServerError.USERS_NOT_FOUND.getMessage());
            }
            return new Response(ResponseType.SUCCESS, gson.toJson(users));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAllDoctors(Request request) {
        try {
            List<Doctor> doctors = doctorService.findAllEntities();
            if (doctors == null || doctors.isEmpty()) {
                return new Response(ResponseType.ERROR, ServerError.DOCTORS_NOT_FOUND.getMessage());
            }
            return new Response(ResponseType.SUCCESS, gson.toJson(doctors));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAllPatients(Request request) {
        try {
            List<Patient> patients = patientService.findAllEntities();
            if (patients == null || patients.isEmpty()) {
                return new Response(ResponseType.ERROR, ServerError.PATIENTS_NOT_FOUND.getMessage());
            }
            return new Response(ResponseType.SUCCESS, gson.toJson(patients));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAllAppointments(Request request) {
        try {
            List<Appointment> appointments = appointmentService.findAllEntities();
            if (appointments == null || appointments.isEmpty()) {
                return new Response(ResponseType.ERROR, ServerError.APPOINTMENTS_NOT_FOUND.getMessage());
            }
            return new Response(ResponseType.SUCCESS, gson.toJson(appointments));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleUpdateWorkDay(Request request) {
        try {
            Object[] data = gson.fromJson(request.getMessage(), Object[].class);

            int doctorId = ((Double) data[0]).intValue();
            WorkDayDTO workDay = gson.fromJson(gson.toJson(data[1]), WorkDayDTO.class);

            WorkingSchedule schedule = workingScheduleService.findByDoctorId(doctorId);
            if (schedule == null) {
                return new Response(ResponseType.ERROR, ServerError.WORKING_SCHEDULE_NOT_FOUND.getMessage());
            }

            WorkDay existingWorkDay = workDayService.findByScheduleIdAndDay(schedule.getId(), workDay.getDay());
            if (existingWorkDay == null) {
                return new Response(ResponseType.ERROR, ServerError.WORK_DAY_NOT_FOUND.getMessage());
            }

            existingWorkDay.setStartTime(workDay.getStartTime());
            existingWorkDay.setEndTime(workDay.getEndTime());
            workDayService.updateEntity(existingWorkDay);

            return new Response(ResponseType.SUCCESS, gson.toJson(SuccessMessage.WORK_DAY_UPDATED.getMessage()));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAddWorkDay(Request request) {
        try {
            Object[] data = gson.fromJson(request.getMessage(), Object[].class);

            int doctorId = ((Double) data[0]).intValue();
            WorkDayDTO workDay = gson.fromJson(gson.toJson(data[1]), WorkDayDTO.class);

            WorkingSchedule schedule = workingScheduleService.findByDoctorId(doctorId);
            if (schedule == null) {
                return new Response(ResponseType.ERROR, ServerError.WORKING_SCHEDULE_NOT_FOUND.getMessage());
            }

            WorkDay newWorkDay = new WorkDay(
                    workDay.getDay(),
                    workDay.getStartTime(),
                    workDay.getEndTime(),
                    schedule
            );

            workDayService.saveEntity(newWorkDay);

            return new Response(ResponseType.SUCCESS, gson.toJson(SuccessMessage.WORK_DAY_ADDED.getMessage()));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleDeleteWorkDay(Request request) {
        try {
            Object[] data = gson.fromJson(request.getMessage(), Object[].class);

            int doctorId = ((Double) data[0]).intValue();
            WorkDayDTO workDay = gson.fromJson(gson.toJson(data[1]), WorkDayDTO.class);

            WorkingSchedule schedule = workingScheduleService.findByDoctorId(doctorId);
            if (schedule == null) {
                return new Response(ResponseType.ERROR, ServerError.WORKING_SCHEDULE_NOT_FOUND.getMessage());
            }

            WorkDay workDayToDelete = workDayService.findByScheduleIdAndDay(schedule.getId(), workDay.getDay());
            if (workDayToDelete == null) {
                return new Response(ResponseType.ERROR, ServerError.WORK_DAY_NOT_FOUND.getMessage());
            }

            workDayService.deleteEntity(workDayToDelete.getId());

            return new Response(ResponseType.SUCCESS, gson.toJson(SuccessMessage.WORK_DAY_DELETED.getMessage()));
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAddDoctor(Request request) {
        try {
            Doctor doctor = gson.fromJson(request.getMessage(), Doctor.class);
            doctorService.saveEntity(doctor);

            WorkingSchedule schedule = new WorkingSchedule();
            schedule.setDoctor(doctor);
            workingScheduleService.saveEntity(schedule);

            return new Response(ResponseType.SUCCESS, SuccessMessage.DOCTOR_REGISTERED.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    private Response handleAddPatient(Request request) {
        try {
            Patient patient = gson.fromJson(request.getMessage(), Patient.class);
            patientService.saveEntity(patient);
            return new Response(ResponseType.SUCCESS, SuccessMessage.PATIENT_REGISTERED.getMessage());
        } catch (IllegalArgumentException e) {
            return new Response(ResponseType.ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseType.ERROR, ServerError.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

}