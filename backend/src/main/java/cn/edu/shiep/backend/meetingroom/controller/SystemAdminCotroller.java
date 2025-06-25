package cn.edu.shiep.backend.meetingroom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.shiep.backend.meetingroom.dto.UserDTO;
import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.dto.request.SignupRequest;
import cn.edu.shiep.backend.meetingroom.service.AdminUserService;

@RestController
@RequestMapping("/api/admin/users")
public class SystemAdminCotroller {
    
    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestBody String roleName) {
        ERole role = ERole.valueOf(roleName);
        UserDTO updatedUser = adminUserService.updateUserRole(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok("用户已成功删除");
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody SignupRequest signupRequest, @RequestParam ERole role) {
        UserDTO user = adminUserService.createUser(signupRequest, role);
        return ResponseEntity.ok(user);
    }
}
