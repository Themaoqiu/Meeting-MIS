package cn.edu.shiep.backend.meetingroom.service;

import cn.edu.shiep.backend.meetingroom.dto.request.ReservationRequest;
import cn.edu.shiep.backend.meetingroom.dto.response.ReservationResponse;
import cn.edu.shiep.backend.meetingroom.entity.ConferenceRoom;
import cn.edu.shiep.backend.meetingroom.entity.Equipment;
import cn.edu.shiep.backend.meetingroom.entity.Reservation;
import cn.edu.shiep.backend.meetingroom.entity.User;
import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import cn.edu.shiep.backend.meetingroom.repository.ConferenceRoomRepository;
import cn.edu.shiep.backend.meetingroom.repository.EquipmentRepository;
import cn.edu.shiep.backend.meetingroom.repository.ReservationRepository;
import cn.edu.shiep.backend.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private EquipmentRepository equipmentRepository;
    
    // 新建预约
    @Transactional
    public ReservationResponse createReservation(ReservationRequest requestDTO, Long userId){
        ConferenceRoom room = conferenceRoomRepository.findById(requestDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("会议室未找到, ID: " + requestDTO.getRoomId()));
        User user = userRepository.findById(userId)

                .orElseThrow(() -> new RuntimeException("用户未找到, ID: " + userId));

        if(requestDTO.getPersonNum() > room.getCapacity()){
            throw new RuntimeException("参会人数(" + requestDTO.getPersonNum() + ")超过会议室容量(" + room.getCapacity() + ")");
        }
        if(!requestDTO.getStartTime().isBefore(requestDTO.getEndTime())){
            throw new RuntimeException("开始时间必须在结束时间之前");
        }
        long conflictingReservations = reservationRepository.countConflictingReservations(requestDTO.getRoomId(), requestDTO.getStartTime(), requestDTO.getEndTime());
        if(conflictingReservations > 0){
            throw new RuntimeException("该时间段已被预约，存在时间冲突");
        }
        Reservation newReservation = new Reservation();
        if (requestDTO.getEquipmentIds() != null && !requestDTO.getEquipmentIds().isEmpty()) {
            List<Equipment> selectedEquipments = equipmentRepository.findAllById(requestDTO.getEquipmentIds());
            newReservation.setEquipments(new HashSet<>(selectedEquipments));
        }
        newReservation.setConferenceRoom(room);
        newReservation.setUser(user);
        newReservation.setTheme(requestDTO.getTheme());
        newReservation.setPersonNum(requestDTO.getPersonNum());
        newReservation.setStartTime(requestDTO.getStartTime());
        newReservation.setEndTime(requestDTO.getEndTime());
        newReservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(newReservation);
        noticeService.createAndSendBookingConfirmation(savedReservation);
        return toResponseDTO(savedReservation);
    }

    public List<ReservationResponse> getMyReservations(Long userId) {
        return reservationRepository.findByUser_UserId(userId).stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

    public List<ReservationResponse> getReservationsByDay(LocalDateTime day) {
        LocalDateTime startTime = day.toLocalDate().atStartOfDay();
        LocalDateTime endTime = day.toLocalDate().atTime(23, 59, 59);
        return reservationRepository.findByStartTimeBetween(startTime, endTime).stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("预约记录未找到, ID: " + reservationId));
        // 现在只有本人能取消
        // TODO：判断isadmin()使得管理员也能取消
        if(!Objects.equals(reservation.getUser().getUserId(), userId)){
            throw new RuntimeException("权限不足，您无法取消不属于您的预约");
        }
        if(reservation.getStatus() != ReservationStatus.CONFIRMED){
            throw new RuntimeException("该预约状态为" + reservation.getStatus() + ", 无法取消");
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }
    private ReservationResponse toResponseDTO(Reservation reservation) {
        return ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .theme(reservation.getTheme())
                .personNum(reservation.getPersonNum())
                .startTime(reservation.getStartTime().toInstant(ZoneOffset.UTC))
                .endTime(reservation.getEndTime().toInstant(ZoneOffset.UTC))
                .status(reservation.getStatus())
                .roomName(reservation.getConferenceRoom().getName())
                .userName(reservation.getUser().getName())
                .userEmail(reservation.getUser().getEmail())
                .build();
    }

    public List<ReservationResponse> getReservationsByDayRange(LocalDateTime start, LocalDateTime end, Long roomId) {
        List<Reservation> reservations;
        if (roomId != null) {
            reservations = reservationRepository.findByConferenceRoom_RoomIdAndStartTimeBetween(roomId, start, end);
        } else {
            reservations = reservationRepository.findByStartTimeBetween(start, end);
        }
        return reservations.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

   
}

