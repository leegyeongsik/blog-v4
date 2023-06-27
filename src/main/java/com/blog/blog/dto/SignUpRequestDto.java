package com.blog.blog.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SignUpRequestDto {
    @NotBlank(message = "이름을 기입하셔야합니다")
    @Pattern(regexp = "^[a-z0-9]{4,10}$" , message = "이름은 알파벳 소문자 , 숫자만 가능하고 4글자 10글자 사이여야 합니다 ")
    String username;
    @NotBlank(message = "비밀번호를 기입하셔야합니다")
    @Pattern(regexp = "^[a-zA-z0-9]{8,15}$", message = "패스워드는 알파벳 대소문자 , 숫자만 가능하고 8글자 15글자 사이여야 합니다")
    String password;

    private boolean admin = false;
    private String adminToken = "";

}
