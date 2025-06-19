package cn.edu.shiep.backend.meetingroom.dto;

import cn.edu.shiep.backend.meetingroom.enums.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConferenceRoomDTO {
    private Integer roomId;
    private String name;
    private int capacity;
    private RoomStatus status;
    private List<EquipmentDTO> equipments;
}
