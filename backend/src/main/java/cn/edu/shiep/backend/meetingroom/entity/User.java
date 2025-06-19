package cn.edu.shiep.backend.meetingroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String phone;

    public User(String userName, String email, String password, String phone) {
        this.email = email;
        this.name = userName;
        this.password = password;
        this.phone = phone;
    }

}
