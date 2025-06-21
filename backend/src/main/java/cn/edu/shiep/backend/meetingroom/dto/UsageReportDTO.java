package cn.edu.shiep.backend.meetingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsageReportDTO { 
    private long totalBookings;
    private Double totalHoursBooked;
    private double cancellationRate;
}
