package cn.edu.shiep.backend.meetingroom.dto.response;

import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
@Data
@Builder
public class ReservationResponse {
    private Long reservationId;
    private String theme;
    private int personNum;
    private Instant startTime;
    private Instant endTime;
    private ReservationStatus status;
    private String roomName;
    private String userName;
    private String userEmail;
}
