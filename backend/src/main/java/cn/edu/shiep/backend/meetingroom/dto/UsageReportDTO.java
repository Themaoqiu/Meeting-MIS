package cn.edu.shiep.backend.meetingroom.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UsageReportDTO { 
    private long totalBookings;
    private Double totalHoursBooked;
    private double cancellationRate;
    private List<RoomUsage> usageByRoom;
    private List<UserActivity> userActivity;

    // 单个会议室使用情况
    @Data
    @Builder
    public static class RoomUsage {
        private String roomName;
        private long bookingCount;
        private double totalHours;
    }

    // 用户活跃度
    @Data
    @Builder
    public static class UserActivity {
        private String userName;
        private long bookingCount;
    }
}
