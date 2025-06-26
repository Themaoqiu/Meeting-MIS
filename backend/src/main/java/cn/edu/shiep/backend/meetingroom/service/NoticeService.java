package cn.edu.shiep.backend.meetingroom.service;

import cn.edu.shiep.backend.meetingroom.entity.Notice;
import cn.edu.shiep.backend.meetingroom.entity.Reservation;
import cn.edu.shiep.backend.meetingroom.enums.NoticeStatus;
import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import cn.edu.shiep.backend.meetingroom.repository.NoticeRepository;
import cn.edu.shiep.backend.meetingroom.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public void createAndSendBookingConfirmation(Reservation reservation){
        String content = String.format(
                "%s, 你好！您已成功预约会议室【%s】，时间为 %s 到 %s，会议主题为【%s】。",
                reservation.getUser().getName(),
                reservation.getConferenceRoom().getName(),
                reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                reservation.getTheme()
        );
        createNotice(reservation, content);
    }
    @Scheduled(cron = "0 * * * * ?")
    public void sendMeetingReminders(){
        System.out.println("Executing reminder check at " + LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime remindStart = now.plusMinutes(15);
        LocalDateTime remindEnd = now.plusMinutes(16);
        List<Reservation> upcomingReservations = reservationRepository.findByStartTimeBetweenAndStatus(remindStart, remindEnd, ReservationStatus.CONFIRMED);
        for(Reservation reservation : upcomingReservations){
            String content = String.format("【会议提醒】您预约的会议【%s】即将在15分钟后于会议室【%s】开始。",
                    reservation.getTheme(),
                    reservation.getConferenceRoom().getName());
            createNotice(reservation, content);
        }
    }
    public void createAndSendCancelNotice(Reservation reservation) {
        String content = String.format(
            "【会议已取消预约】您预约的会议【%s】（会议室：%s，时间：%s - %s）已被取消。",
            reservation.getTheme(),
            reservation.getConferenceRoom().getName(),
            reservation.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        createNotice(reservation, content);
    }
    private void createNotice(Reservation reservation, String content){
        Notice notice = new Notice();
        notice.setReservation(reservation);
        notice.setRecipient(reservation.getUser());
        notice.setContent(content);
        notice.setCreatedTime(LocalDateTime.now());
        notice.setSendStatus(NoticeStatus.PENDING);
        noticeRepository.save(notice);
        notice.setSendStatus(NoticeStatus.SENT);
        notice.setSentTime(LocalDateTime.now());
        noticeRepository.save(notice);
    }
}
