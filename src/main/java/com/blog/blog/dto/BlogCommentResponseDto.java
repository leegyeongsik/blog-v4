package com.blog.blog.dto;

import lombok.Getter;

@Getter
public class BlogCommentResponseDto {
        private String comment;


    public BlogCommentResponseDto(String comment){
        this.comment = comment;
    }
}
