package cn.edu.shiep.backend.meetingroom.dto.response;

import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class ReservationResponse {
    private Long reservationId;
    private String theme;
    private int personNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private String roomName;
    private String userName;
    private String userEmail;
}
