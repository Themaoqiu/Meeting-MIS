package cn.edu.shiep.backend.meetingroom.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NoticeDTO {
    private Long id;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime sentTime;
    private String sendStatus;
    private String reservationTheme;
    private String reservationRoomName;
    private LocalDateTime reservationStartTime;
    private LocalDateTime reservationEndTime;
} 