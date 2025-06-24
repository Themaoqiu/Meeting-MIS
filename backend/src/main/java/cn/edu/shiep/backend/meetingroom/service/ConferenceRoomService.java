package cn.edu.shiep.backend.meetingroom.service;

import cn.edu.shiep.backend.meetingroom.dto.ConferenceRoomAdminDTO;
import cn.edu.shiep.backend.meetingroom.dto.ConferenceRoomDTO;
import cn.edu.shiep.backend.meetingroom.dto.EquipmentDTO;
import cn.edu.shiep.backend.meetingroom.dto.RoomStatusViewDTO;
import cn.edu.shiep.backend.meetingroom.entity.ConferenceRoom;
import cn.edu.shiep.backend.meetingroom.entity.Equipment;
import cn.edu.shiep.backend.meetingroom.entity.Reservation;
import cn.edu.shiep.backend.meetingroom.enums.RoomStatus;
import cn.edu.shiep.backend.meetingroom.repository.ConferenceRoomRepository;
import cn.edu.shiep.backend.meetingroom.repository.EquipmentRepository;
import cn.edu.shiep.backend.meetingroom.repository.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConferenceRoomService {
    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    
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
                .equipments(room.getEquipments().stream().map(equipment ->EquipmentDTO.builder()
                                .equipmentId(equipment.getEquipmentId())
                                .deviceName(equipment.getDeviceName())
                                .type(equipment.getType())
                                .status(equipment.getStatus())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public ConferenceRoomDTO addRoom(ConferenceRoomAdminDTO dto){
        ConferenceRoom room = new ConferenceRoom();
        room.setName(dto.getName());
        room.setCapacity(dto.getCapacity());
        room.setRoomStatus(dto.getStatus());
        
        if(dto.getEquipments() != null){
            List<Equipment> equipmentList = new ArrayList<>();
            for(EquipmentDTO equipDTO : dto.getEquipments()){
                Equipment equipment = new Equipment();
                equipment.setDeviceName(equipDTO.getDeviceName());
                equipment.setStatus(equipDTO.getStatus());
                equipment.setType(equipDTO.getType());
                equipment.setConferenceRoom(room);
                equipmentList.add(equipment);
            }
            room.setEquipments(equipmentList); 
        }
        
        ConferenceRoom savedRoom = conferenceRoomRepository.save(room);
        return toConferenceRoomDTO(savedRoom);
    }

    @Transactional
    public ConferenceRoomDTO updateRoom(Long roomId, ConferenceRoomAdminDTO dto){
        ConferenceRoom room = conferenceRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("会议室不存在"));
        room.setName(dto.getName());
        room.setCapacity(dto.getCapacity());
        room.setRoomStatus(dto.getStatus());
        if(dto.getEquipments() != null){
            room.getEquipments().clear();
            List<Equipment> newEquipmentsList = dto.getEquipments().stream().map(equipDTO -> {
                Equipment equipment = new Equipment();
                equipment.setDeviceName(equipDTO.getDeviceName());
                equipment.setStatus(equipDTO.getStatus());
                equipment.setType(equipDTO.getType());
                equipment.setConferenceRoom(room);
                return equipment;
            }).collect(Collectors.toList());
            room.getEquipments().addAll(newEquipmentsList);
        }
    return toConferenceRoomDTO(conferenceRoomRepository.save(room));
    }

    public List<RoomStatusViewDTO> getRoomStatusViewDashBoard(){
        List<ConferenceRoom> rooms = conferenceRoomRepository.findAll();
        return rooms.stream().<RoomStatusViewDTO>map(room -> {
            RoomStatus status = room.getRoomStatus();
            String theme = null;
            if(status == RoomStatus.AVAILABLE){
                List<Reservation> activeReservations = reservationRepository.findActiveReservationForRoom(room.getRoomId(), LocalDateTime.now(ZoneOffset.UTC));

            if (!activeReservations.isEmpty()) {
                // If a reservation is active, update the status and get the theme
                status = RoomStatus.OCCUPIED;
                // Get the theme from the first active reservation in the list
                theme = activeReservations.get(0).getTheme();
            }
            }
            return RoomStatusViewDTO.builder()
                    .roomId(room.getRoomId())
                    .name(room.getName())
                    .capacity(room.getCapacity())
                    .status(status)
                    .currentBookingTheme(theme)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        if (!conferenceRoomRepository.existsById(roomId)) {
            throw new RuntimeException("会议室不存在, ID: " + roomId);
        }
        // 检查是否有未来的预约
        long upcomingReservations = reservationRepository.countByConferenceRoom_RoomIdAndEndTimeAfter(roomId, LocalDateTime.now());
        if (upcomingReservations > 0) {
            throw new RuntimeException("无法删除！该会议室尚有未完成的预约。");
        }
        conferenceRoomRepository.deleteById(roomId);
    }
}
