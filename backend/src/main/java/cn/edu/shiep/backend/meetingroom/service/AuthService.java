package cn.edu.shiep.backend.meetingroom.service;

import cn.edu.shiep.backend.meetingroom.dto.UserDTO;
import cn.edu.shiep.backend.meetingroom.dto.request.LoginRequest;
import cn.edu.shiep.backend.meetingroom.dto.request.SignupRequest;
import cn.edu.shiep.backend.meetingroom.entity.Roles;
import cn.edu.shiep.backend.meetingroom.entity.User;
import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.repository.RoleRepository;
import cn.edu.shiep.backend.meetingroom.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void register(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("错误: 邮箱已被注册!");
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encodedPassword, signupRequest.getPhone());

        // 默认设定为 USER 角色
        Roles userRole = roleRepository.findByName(ERole.USER).orElseThrow(() -> new RuntimeException("错误: 默认用户角色 'USER' 未在数据库中找到。"));
        user.setRole(userRole);

        userRepository.save(user);
    }

    public UserDTO login(LoginRequest loginRequest, HttpServletRequest request) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("错误: 用户不存在。"));

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("错误: 密码无效。");
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getUserId());

        return convertToUserDTO(user);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public UserDTO checkStatus(HttpSession session) {
        // 会话中获取userid转换成int，如果为null说明没登录
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userRepository.findById(Long.valueOf(userId)).map(this::convertToUserDTO).orElse(null);
    }

    private UserDTO convertToUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                Set.of(user.getRole().getName().name())
        );
    }
}