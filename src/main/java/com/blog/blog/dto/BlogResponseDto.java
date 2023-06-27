package com.blog.blog.dto;

import com.blog.blog.entity.Blog;
import com.blog.blog.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BlogResponseDto {
    private Long id;
    private String title;
    private String username;
    private String content;
    private String password;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> commentList;
    public BlogResponseDto (Blog blog ){

        this.id = blog.getId();
        this.title = blog.getTitle();
        this.username = blog.getUsername();
        this.content = blog.getContent();
        this.password = blog.getPassword();
        this.createAt = blog.getCreatedAt();
        this.modifiedAt = blog.getModifiedAt();
    }
    public BlogResponseDto (Blog blog , List<CommentResponseDto> commentList){

        this.id = blog.getId();
        this.title = blog.getTitle();
        this.username = blog.getUsername();
        this.content = blog.getContent();
        this.password = blog.getPassword();
        this.createAt = blog.getCreatedAt();
        this.modifiedAt = blog.getModifiedAt();
        this.commentList = commentList;
    }
}
