package com.blog.blog.Service;

import com.blog.blog.Repository.UserRepository;
import com.blog.blog.dto.LoginRequestDto;
import com.blog.blog.dto.SignUpRequestDto;
import com.blog.blog.entity.User;
import com.blog.blog.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public String signup(SignUpRequestDto signUpRequestDto , HttpServletResponse response) throws IOException {
        String username = signUpRequestDto.getUsername();
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            new ObjectMapper().writeValue(response.getOutputStream(), "중복된 사용자가 존재합니다");
            throw new IllegalArgumentException("중복된 사용자가 존재합니다");
        }

        // 사용자 role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signUpRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(signUpRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password , role);
        userRepository.save(user);
        return "회원가입 성공";

    }

    public String login(LoginRequestDto requestDto) {
        return null;

    }
}
