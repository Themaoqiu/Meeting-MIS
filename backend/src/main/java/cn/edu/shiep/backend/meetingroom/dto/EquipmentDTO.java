package cn.edu.shiep.backend.meetingroom.dto;

import cn.edu.shiep.backend.meetingroom.enums.EquipmentStatus;
import cn.edu.shiep.backend.meetingroom.enums.EquipmentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EquipmentDTO {
    private Integer equipmentId;
    private String deviceName;
    private EquipmentType type;
    private EquipmentStatus status;
}
