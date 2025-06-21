package cn.edu.shiep.backend.meetingroom.dto;

import java.util.List;

import cn.edu.shiep.backend.meetingroom.enums.RoomStatus;
import lombok.Data;

@Data
public class ConferenceRoomAdminDTO {
    private String name;
    private int capacity; // 容量
    private RoomStatus status;
    private List<EquipmentDTO> equipments;
}
