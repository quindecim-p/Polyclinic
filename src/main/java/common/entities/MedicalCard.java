package common.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MedicalCards")
public class MedicalCard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public MedicalCard() {}

    public MedicalCard(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

}