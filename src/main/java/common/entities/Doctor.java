package common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Doctors")
public class Doctor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String specialization;

    @Column(name = "office_number", nullable = false)
    private int officeNumber;

    @Embedded
    private PersonData personData;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Doctor() {}

    public Doctor(String specialization, int officeNumber, PersonData personData, User user) {
        this.specialization = specialization;
        this.officeNumber = officeNumber;
        this.personData = personData;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpecialization() { return specialization; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getOfficeNumber() { return officeNumber; }

    public void setOfficeNumber(int officeNumber) { this.officeNumber = officeNumber; }

    public PersonData getPersonData() { return personData; }

    public void setPersonData(PersonData personData) { this.personData = personData; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

}