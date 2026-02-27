CREATE DATABASE IF NOT EXISTS polyclinic;

USE polyclinic;

CREATE TABLE Roles (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       role_type VARCHAR(255) NOT NULL
);

CREATE TABLE Users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id INT NOT NULL,
                       FOREIGN KEY (role_id) REFERENCES Roles(id)
);

CREATE TABLE MedicalCards (
                              id INT PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE Patients (
                          patient_id INT PRIMARY KEY AUTO_INCREMENT,
                          medical_card_id INT NOT NULL UNIQUE,
                          user_id INT NOT NULL,
                          FOREIGN KEY (medical_card_id) REFERENCES MedicalCards(id),
                          FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE Doctors (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         specialization VARCHAR(255) NOT NULL,
                         office_number INT NOT NULL,
                         user_id INT NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE WorkingSchedules (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  doctor_id INT NOT NULL UNIQUE,
                                  FOREIGN KEY (doctor_id) REFERENCES Doctors(id)
);

CREATE TABLE WorkDays (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          day VARCHAR(255) NOT NULL,
                          start_time TIME NOT NULL,
                          end_time TIME NOT NULL,
                          working_schedule_id INT NOT NULL,
                          FOREIGN KEY (working_schedule_id) REFERENCES WorkingSchedules(id)
);

CREATE TABLE Appointments (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              appointment_date DATETIME NOT NULL,
                              status VARCHAR(255) NOT NULL,
                              symptoms VARCHAR(255) NOT NULL,
                              medical_card_id INT NOT NULL,
                              doctor_id INT NOT NULL,
                              FOREIGN KEY (medical_card_id) REFERENCES MedicalCards(id),
                              FOREIGN KEY (doctor_id) REFERENCES Doctors(id)
);

CREATE TABLE Reference (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           details VARCHAR(255) NOT NULL,
                           valid_from DATE NOT NULL,
                           valid_until DATE NOT NULL,
                           appointment_id INT NOT NULL,
                           FOREIGN KEY (appointment_id) REFERENCES Appointments(id)
);

CREATE TABLE Diagnoses (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(255) NOT NULL,
                           description VARCHAR(1000),
                           appointment_id INT NOT NULL,
                           FOREIGN KEY (appointment_id) REFERENCES Appointments(id)
);

CREATE TABLE Prescriptions (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               instructions VARCHAR(255) NOT NULL,
                               appointment_id INT NOT NULL,
                               FOREIGN KEY (appointment_id) REFERENCES Appointments(id)
);