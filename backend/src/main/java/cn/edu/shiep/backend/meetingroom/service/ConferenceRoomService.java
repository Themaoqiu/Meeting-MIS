package cn.edu.shiep.backend.meetingroom.service;

import cn.edu.shiep.backend.meetingroom.dto.ConferenceRoomDTO;
import cn.edu.shiep.backend.meetingroom.dto.EquipmentDTO;
import cn.edu.shiep.backend.meetingroom.entity.ConferenceRoom;
import cn.edu.shiep.backend.meetingroom.repository.ConferenceRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConferenceRoomService {
    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;
    public List<ConferenceRoomDTO> getAllRoomsWithEquipment() {
        return conferenceRoomRepository.findAll().stream()
                .map(this::toConferenceRoomDTO)
                .collect(Collectors.toList());
    }
    private ConferenceRoomDTO toConferenceRoomDTO(ConferenceRoom room) {
        return ConferenceRoomDTO.builder()
                .roomId(room.getRoomId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .status(room.getRoomStatus())
                .equipments(room.getEquipments().stream().map(equipment ->
                        EquipmentDTO.builder()
                                .equipmentId(equipment.getEquipmentId())
                                .deviceName(equipment.getDeviceName())
                                .type(equipment.getType())
                                .status(equipment.getStatus())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
