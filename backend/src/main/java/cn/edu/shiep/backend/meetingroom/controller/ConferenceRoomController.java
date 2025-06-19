package cn.edu.shiep.backend.meetingroom.controller;

import cn.edu.shiep.backend.meetingroom.dto.ConferenceRoomDTO;
import cn.edu.shiep.backend.meetingroom.entity.ConferenceRoom;
import cn.edu.shiep.backend.meetingroom.repository.ConferenceRoomRepository;
import cn.edu.shiep.backend.meetingroom.service.ConferenceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class ConferenceRoomController {
    @Autowired
    private ConferenceRoomService conferenceRoomService;
    @GetMapping
    public ResponseEntity<List<ConferenceRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(conferenceRoomService.getAllRoomsWithEquipment());
    }
}
