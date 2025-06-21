package cn.edu.shiep.backend.meetingroom.service;

import cn.edu.shiep.backend.meetingroom.dto.UserDTO;
import cn.edu.shiep.backend.meetingroom.dto.request.LoginRequest;
import cn.edu.shiep.backend.meetingroom.dto.request.SignupRequest;
import cn.edu.shiep.backend.meetingroom.entity.Roles;
import cn.edu.shiep.backend.meetingroom.entity.User;
import cn.edu.shiep.backend.meetingroom.enums.ERole;
import cn.edu.shiep.backend.meetingroom.repository.RoleRepository;
import cn.edu.shiep.backend.meetingroom.repository.UserRepository;
import cn.edu.shiep.backend.meetingroom.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


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
        // 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 将认证信息设置到 SecurityContext 中，Spring Security 会自动将其保存到 HttpSession
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        
        // request.getSession(true) 确保了 Session 被创建
        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", context);


        // 从认证信息中获取 UserDetails
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("错误: 用户不存在。"));

        return convertToUserDTO(user);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public UserDTO checkStatus() {
        // 直接从 SecurityContextHolder 获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId()).map(this::convertToUserDTO).orElse(null);
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