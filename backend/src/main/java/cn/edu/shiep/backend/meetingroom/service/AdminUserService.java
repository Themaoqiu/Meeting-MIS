package cn.edu.shiep.backend.meetingroom.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cn.edu.shiep.backend.meetingroom.dto.UserDTO;
import cn.edu.shiep.backend.meetingroom.dto.request.SignupRequest;
import cn.edu.shiep.backend.meetingroom.entity.Roles;
import cn.edu.shiep.backend.meetingroom.entity.User;
import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.repository.RoleRepository;
import cn.edu.shiep.backend.meetingroom.repository.UserRepository;

@Service
public class AdminUserService {
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(SignupRequest signupRequest, ERole roleName) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("错误: 邮箱已被注册!");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encodedPassword, signupRequest.getPhone());

        Roles role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("错误: 角色 '" + roleName + "' 不存在"));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return toUserDTO(savedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDTO updateUserRole(Long userId, ERole roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Roles role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        return toUserDTO(userRepository.save(user));
    }
    
    private UserDTO toUserDTO(User user) {
        return UserDTO.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .roles(Collections.singleton(user.getRole().getName().name()))
            .build();
    }
}
