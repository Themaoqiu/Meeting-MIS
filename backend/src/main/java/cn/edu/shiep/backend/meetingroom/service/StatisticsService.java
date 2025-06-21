package cn.edu.shiep.backend.meetingroom.service;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.shiep.backend.meetingroom.dto.UsageReportDTO;
import cn.edu.shiep.backend.meetingroom.enums.ReservationStatus;
import cn.edu.shiep.backend.meetingroom.repository.ReservationRepository;

@Service
public class StatisticsService {
    @Autowired
    private ReservationRepository reservationRepository;

    public UsageReportDTO generateUsageReport(int year, int month) {
        LocalDateTime start = YearMonth.of(year, month).atDay(1).atStartOfDay();

        LocalDateTime end = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        long totalBookings = reservationRepository.countByStartTimeBetweenAndStatus(start, end, ReservationStatus.CONFIRMED);

        long canceledBookings = reservationRepository.countByStartTimeBetweenAndStatus(start, end, ReservationStatus.CANCELED);

        double cancellationRate = totalBookings > 0 ? (double) canceledBookings / (totalBookings + canceledBookings) : 0;
        
        Double totalHours = reservationRepository.getTotalBookedHours(start, end);

        return new UsageReportDTO(totalBookings, totalHours, cancellationRate);
    }
}