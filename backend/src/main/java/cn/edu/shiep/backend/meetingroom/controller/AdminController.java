package cn.edu.shiep.backend.meetingroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.shiep.backend.meetingroom.dto.*;
import cn.edu.shiep.backend.meetingroom.service.ConferenceRoomService;
import cn.edu.shiep.backend.meetingroom.service.StatisticsService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired 
    private ConferenceRoomService conferenceRoomService;

    @Autowired 
    private StatisticsService statisticsService;

    @GetMapping("/rooms/status")
    @PreAuthorize("hasRole('ROOM_ADMIN')")
    public ResponseEntity<List<RoomStatusViewDTO>> getRoomStatusDashboard() {
        return ResponseEntity.ok(conferenceRoomService.getRoomStatusViewDashBoard());
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ROOM_ADMIN')")
    public ResponseEntity<ConferenceRoomDTO> addRoom(@RequestBody ConferenceRoomAdminDTO dto) {
        return ResponseEntity.ok(conferenceRoomService.addRoom(dto));
    }
    
    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasRole('ROOM_ADMIN')")
    public ResponseEntity<ConferenceRoomDTO> updateRoom(@PathVariable Long id, @RequestBody ConferenceRoomAdminDTO dto) {
        return ResponseEntity.ok(conferenceRoomService.updateRoom(id, dto));
    }

    @GetMapping("/statistics/report")
    @PreAuthorize("hasRole('ROOM_ADMIN')")
    public ResponseEntity<UsageReportDTO> getUsageReport(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(statisticsService.generateUsageReport(year, month));
    }
    
}