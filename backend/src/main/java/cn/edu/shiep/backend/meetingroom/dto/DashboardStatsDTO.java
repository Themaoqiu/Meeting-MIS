package cn.edu.shiep.backend.meetingroom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalRooms;
    private long todaysMeetings;
    private long myUpcomingMeetings;
}