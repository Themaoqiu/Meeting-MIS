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

    // 查会议室在指定时间段冲突预约的数量
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.conferenceRoom.roomId = :roomId " +
            "AND r.status = 'CONFIRMED' " +
            "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    long countConflictingReservations(@Param("roomId") Long roomId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.conferenceRoom.roomId = :roomId " +
            "AND r.status = 'CONFIRMED' " +
            "AND (r.startTime <= :now AND r.endTime > :now)")
    List<Reservation> findActiveReservationForRoom(@Param("roomId") Long roomId,@Param("now") LocalDateTime now);

    @Query("SELECT COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.startTime, r.endTime)), 0) " +
          "FROM Reservation r " +
          "WHERE r.startTime BETWEEN :start AND :end " +
          "AND r.status = cn.edu.shiep.backend.meetingroom.enums.ReservationStatus.CONFIRMED")
    Double getTotalBookedHours(LocalDateTime start, LocalDateTime end);    
}
