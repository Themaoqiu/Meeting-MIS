package cn.edu.shiep.backend.meetingroom.controller;

import cn.edu.shiep.backend.meetingroom.dto.DashboardStatsDTO;
import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import cn.edu.shiep.backend.meetingroom.repository.ConferenceRoomRepository;
import cn.edu.shiep.backend.meetingroom.repository.ReservationRepository;
import cn.edu.shiep.backend.meetingroom.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ConferenceRoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats(@AuthenticationPrincipal UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        Long userId = userDetailsImpl.getId();

        long totalRooms = roomRepository.count();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long todaysMeetings = reservationRepository.countByUser_UserIdAndStartTimeBetweenAndStatus(userId, startOfDay, endOfDay, ReservationStatus.CONFIRMED);

        // 查找在当日之后还未结束的会议（不包括当日）
        long myUpcomingMeetings = reservationRepository.countByUser_UserIdAndEndTimeAfterAndStatus(userId, endOfDay, ReservationStatus.CONFIRMED);

        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalRooms(totalRooms)
                .todaysMeetings(todaysMeetings)
                .myUpcomingMeetings(myUpcomingMeetings)
                .build();

        return ResponseEntity.ok(stats);
    }
}