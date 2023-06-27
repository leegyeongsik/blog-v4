package com.blog.blog.dto;

import com.blog.blog.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    Long id;
    String comment;
    LocalDateTime created_at;
    LocalDateTime modified_at;

    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.created_at = comment.getCreatedAt();
        this.modified_at = comment.getModifiedAt();



    }

}
