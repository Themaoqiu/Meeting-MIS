package cn.edu.shiep.backend.meetingroom.controller;

import cn.edu.shiep.backend.meetingroom.dto.UserDTO;
import cn.edu.shiep.backend.meetingroom.dto.request.LoginRequest;
import cn.edu.shiep.backend.meetingroom.dto.request.SignupRequest;
import cn.edu.shiep.backend.meetingroom.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            authService.register(signupRequest);
            return ResponseEntity.ok("用户注册成功!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            UserDTO userDTO = authService.login(loginRequest, request);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("用户登出成功!");
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkLoginStatus(HttpSession session) {
        UserDTO userDTO = authService.checkStatus(session);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.status(401).body("用户未登录");
    }
}