package com.blog.blog.Controller;

import com.blog.blog.Service.UserService;
import com.blog.blog.dto.SignUpRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/user/signup")
    public String signup(@RequestBody  @Valid SignUpRequestDto signUpRequestDto,
                         BindingResult bindingResult ,
                         HttpServletResponse response) throws IOException {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());

            }
            response.setStatus(401);
            return "원하는 회원가입 형식이 아닙니다"+response.getStatus();
        }
        return userService.signup(signUpRequestDto , response);
    }


}
