package cn.edu.shiep.backend.meetingroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SystemSetting {
    @Id
    private String settingKey;
    private String settingValue;
}
