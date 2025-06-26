package cn.edu.shiep.backend.meetingroom.controller;

import cn.edu.shiep.backend.meetingroom.entity.Notice;
import cn.edu.shiep.backend.meetingroom.repository.NoticeRepository;
import cn.edu.shiep.backend.meetingroom.security.services.UserDetailsImpl;
import cn.edu.shiep.backend.meetingroom.dto.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    @GetMapping("/my")
    public ResponseEntity<List<NoticeDTO>> getMyNotices(@AuthenticationPrincipal UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        Long userId = userDetailsImpl.getId();

        List<Notice> notices = noticeRepository.findByRecipient_UserIdOrderByCreatedTimeDesc(userId);
        List<NoticeDTO> dtoList = notices.stream().map(notice -> NoticeDTO.builder()
                .id(notice.getId())
                .content(notice.getContent())
                .createdTime(notice.getCreatedTime())
                .sentTime(notice.getSentTime())
                .sendStatus(notice.getSendStatus() != null ? notice.getSendStatus().name() : null)
                .reservationTheme(notice.getReservation() != null ? notice.getReservation().getTheme() : null)
                .reservationRoomName(notice.getReservation() != null ? notice.getReservation().getConferenceRoom().getName() : null)
                .reservationStartTime(notice.getReservation() != null ? notice.getReservation().getStartTime() : null)
                .reservationEndTime(notice.getReservation() != null ? notice.getReservation().getEndTime() : null)
                .build()
        ).toList();
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> deleteMyNotices(@AuthenticationPrincipal UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        Long userId = userDetailsImpl.getId();
        List<Notice> notices = noticeRepository.findByRecipient_UserIdOrderByCreatedTimeDesc(userId);
        noticeRepository.deleteAll(notices);
        return ResponseEntity.noContent().build();
    }
} 