package cn.edu.shiep.backend.meetingroom.entity;

import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Reservation")
@Getter
@Setter
@NoArgsConstructor
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private String theme;

    @Column(name = "person_num")
    private int personNum;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ConferenceRoom conferenceRoom;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reservation_equipment",
               joinColumns = @JoinColumn(name = "reservation_id"),
               inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    private Set<Equipment> equipments = new HashSet<>();

    
}
