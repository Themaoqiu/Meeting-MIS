package cn.edu.shiep.backend.meetingroom.utils;

import cn.edu.shiep.backend.meetingroom.entity.Roles;
import cn.edu.shiep.backend.meetingroom.entity.User;
import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.repository.RoleRepository;
import cn.edu.shiep.backend.meetingroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUser();
    }
    // 填充用户表中的角色
    private void seedRoles() {
        if (roleRepository.findByName(ERole.USER).isEmpty()) {
            Roles userRole = new Roles();
            userRole.setName(ERole.USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(ERole.ROOM_ADMIN).isEmpty()) {
            Roles roomAdminRole = new Roles();
            roomAdminRole.setName(ERole.ROOM_ADMIN);
            roleRepository.save(roomAdminRole);
        }

        if (roleRepository.findByName(ERole.SYSTEM_ADMIN).isEmpty()) {
            Roles systemAdminRole = new Roles();
            systemAdminRole.setName(ERole.SYSTEM_ADMIN);
            roleRepository.save(systemAdminRole);
        }
    }

    // 创建默认管理员账号
    private void seedAdminUser() {
        if (userRepository.existsByEmail("admin@meeting.com")) {
            return; // 如果管理员已存在，不重复创建
        }

        // 获取ROOM_ADMIN角色
        Roles adminRole = roleRepository.findByName(ERole.ROOM_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROOM_ADMIN角色不存在"));

        // 创建管理员用户
        User adminUser = new User();
        adminUser.setName("会议室管理员");
        adminUser.setEmail("admin@meeting.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setPhone("13800138000");
        adminUser.setRole(adminRole);

        userRepository.save(adminUser);
        System.out.println("默认管理员账号已创建:");
        System.out.println("邮箱: admin@meeting.com");
        System.out.println("密码: admin123");
    }
}