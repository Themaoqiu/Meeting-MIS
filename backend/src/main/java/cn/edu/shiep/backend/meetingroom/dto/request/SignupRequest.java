package cn.edu.shiep.backend.meetingroom.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min=1, max=20)
    private String username;

    @NotBlank
    @Size(min=6, max=50)
    @Email
    private String email;

    @NotBlank
    @Size(min=8, max=50)
    private String password;

    @Size(min=4, max=11)
    private String phone;
}
