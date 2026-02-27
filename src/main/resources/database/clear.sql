USE polyclinic;

SET SQL_SAFE_UPDATES = 0;
SET foreign_key_checks = 0;
DELETE FROM References;
DELETE FROM Diagnoses;
DELETE FROM Prescriptions;
DELETE FROM Appointments;
DELETE FROM WorkDays;
DELETE FROM WorkingSchedules;
DELETE FROM Doctors;
DELETE FROM Patients;
DELETE FROM Users;
DELETE FROM Roles;
DELETE FROM MedicalCards;
SET foreign_key_checks = 1;
SET SQL_SAFE_UPDATES = 1;