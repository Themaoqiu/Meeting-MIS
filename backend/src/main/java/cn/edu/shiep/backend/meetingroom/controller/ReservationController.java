package cn.edu.shiep.backend.meetingroom.controller;

import cn.edu.shiep.backend.meetingroom.dto.request.ReservationRequest;
import cn.edu.shiep.backend.meetingroom.dto.response.ReservationResponse;
import cn.edu.shiep.backend.meetingroom.security.services.UserDetailsImpl;
import cn.edu.shiep.backend.meetingroom.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // @RequestBody 接收 JSON 格式的预约请求数据
    // @AuthenticationPrincipal 是Spring Security 提供的当前登录用户信息
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest dto,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        // 将Spring Security提供的UserDetails向下转型为实现类
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        Long userId = userDetailsImpl.getId();

        ReservationResponse response = reservationService.createReservation(dto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@AuthenticationPrincipal UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        Long userId = userDetailsImpl.getId();

        List<ReservationResponse> reservations = reservationService.getMyReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    // 获取今日会议 (仪表盘功能) - 这个接口通常不需要用户认证信息，因为它展示的是对所有人公开的今日会议
    @GetMapping("/today")
    public ResponseEntity<List<ReservationResponse>> getTodayReservations() {
        List<ReservationResponse> reservations = reservationService.getReservationsByDay(LocalDateTime.now());
        return ResponseEntity.ok(reservations);
    }

    // 取消预约
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        Long userId = userDetailsImpl.getId();

        reservationService.cancelReservation(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/range")
    public ResponseEntity<Object> getReservationsByRange(
        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(reservationService.getReservationsByDayRange(start, end));
    }
}