package com.blog.blog.dto;

import com.blog.blog.entity.BlogCommentLike;
import com.blog.blog.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;
    private Long likes;
    private boolean userBlogCommentLike;


    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.created_at = comment.getCreatedAt();
        this.modified_at = comment.getModifiedAt();
        this.likes = comment.getLikes();

    }
    public CommentResponseDto(Comment comment , Optional<BlogCommentLike> likeOptional){
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.created_at = comment.getCreatedAt();
        this.modified_at = comment.getModifiedAt();
        this.likes = comment.getLikes();
        if (likeOptional.isPresent()) {
            this.userBlogCommentLike = true;
        }
        else {
            this.userBlogCommentLike = false;
        }
    }

}
