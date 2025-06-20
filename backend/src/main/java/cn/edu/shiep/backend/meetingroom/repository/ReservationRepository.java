package cn.edu.shiep.backend.meetingroom.repository;

import cn.edu.shiep.backend.meetingroom.entity.Reservation;
import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_UserId(Long userId);
    List<Reservation> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    List<Reservation> findByStartTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, ReservationStatus status);
    long countByStartTimeBetweenAndStatus(LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status);
    long countByUser_UserIdAndEndTimeAfterAndStatus(Long userId, LocalDateTime now, ReservationStatus status);
    // 看某个会议室在指定时间段是否有冲突预约
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.conferenceRoom.roomId = :roomId " +
            "AND r.status = 'CONFIRMED' " +
            "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    long countConflictingReservations(@Param("roomId") Long roomId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
}
