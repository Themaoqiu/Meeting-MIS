package cn.edu.shiep.backend.meetingroom.utils;

import cn.edu.shiep.backend.meetingroom.entity.Roles;
import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

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
}