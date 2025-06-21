package cn.edu.shiep.backend.meetingroom.dto;

import cn.edu.shiep.backend.meetingroom.enums.RoomStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomStatusViewDTO {
    private Long roomId;
    private String name;
    private int capacity;
    private RoomStatus status;
    private String currentBookingTheme;
}
