package common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Patients")
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medical_card_id", nullable = false)
    private MedicalCard medicalCard;

    @Embedded
    private PersonData personData;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Patient() {}

    public Patient(PersonData personData, MedicalCard medicalCard, User user) {
        this.personData = personData;
        this.medicalCard = medicalCard;
        this.user = user;
    }

    public int getId() { return id;}

    public void setId(int id) { this.id = id; }

    public MedicalCard getMedicalCard() { return medicalCard; }

    public void setMedicalCard(MedicalCard medicalCard) { this.medicalCard = medicalCard; }

    public PersonData getPersonData() { return personData; }

    public void setPersonData(PersonData personData) { this.personData = personData; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

}