package common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "WorkingSchedules")
public class WorkingSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public WorkingSchedule() {}

    public WorkingSchedule(Doctor doctor) {
        this.doctor = doctor;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }

    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

}