package cn.edu.shiep.backend.meetingroom.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationRequest {
    private Long roomId;
    private String theme;
    private int personNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> equipmentIds;
}
