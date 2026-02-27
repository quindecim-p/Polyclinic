package common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Prescriptions")
public class Prescription implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String instructions;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    public Prescription() {}

    public Prescription(String instructions, Appointment appointment) {
        this.instructions = instructions;
        this.appointment = appointment;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getInstructions() { return instructions; }

    public void setInstructions(String instructions) { this.instructions = instructions; }

    public Appointment getAppointment() { return appointment; }

    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

}