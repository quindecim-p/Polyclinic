package common.entities;

import common.enums.types.RoleType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    public Role() {}

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public RoleType getRoleType() { return roleType; }

    public void setRoleType(RoleType roleType) { this.roleType = roleType; }

}