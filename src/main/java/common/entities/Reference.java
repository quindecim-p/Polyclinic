package common.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "`References`")
public class Reference implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String details;

    @Column(name = "valid_from", nullable = false)
    private Date validFrom;

    @Column(name = "valid_until", nullable = false)
    private Date validUntil;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    public Reference() {}

    public Reference(String details, Date validFrom, Date validUntil, Appointment appointment) {
        this.details = details;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.appointment = appointment;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getDetails() { return details; }

    public void setDetails(String details) { this.details = details; }

    public Date getValidFrom() { return validFrom; }

    public void setValidFrom(Date validFrom) { this.validFrom = validFrom; }

    public Date getValidUntil() { return validUntil; }

    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }

    public Appointment getAppointment() { return appointment; }

    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

}
