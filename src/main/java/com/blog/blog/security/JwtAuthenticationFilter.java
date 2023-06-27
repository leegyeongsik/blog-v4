package com.blog.blog.security;

import com.blog.blog.dto.LoginRequestDto;
import com.blog.blog.entity.UserRoleEnum;
import com.blog.blog.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class); // request 를 LoginRequestDto 에 주입

            return getAuthenticationManager().authenticate( // getUsername() , getPassword() , null -> 		principal  , credentials , authority 으로 토큰을 만듬
            new UsernamePasswordAuthenticationToken(    //     토큰 만든걸 AuthenticationManager()보냄 Token을 처리할 수 있는 Authentication Provider를 선택하고
                            requestDto.getUsername(),   //     UserDetailsService의 loadUserByUsername를 실행하고 ( username id로 user를 찾고 그 유저주소로 UserDetailsImpl생성
                            requestDto.getPassword(),   //     생성됬으면 successfulAuthentication , 안됬으면 unsuccessfulAuthentication
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult ) throws IOException {
            String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
            UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
            String token = jwtUtil.createToken(username, role);
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
            String state = "로그인완료"+" " + "status:" +response.getStatus();


            new ObjectMapper().writeValue(response.getOutputStream(), state);




    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed ) throws IOException {
        response.setStatus(400);
        response.getStatus();
        String state = "회원을 찾을 수 없습니다:" +response;

        new ObjectMapper().writeValue(response.getOutputStream(), state);

        ;
    }

}
