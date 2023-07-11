package com.blog.blog.dto;

import com.blog.blog.entity.BlogCommentLike;
import lombok.Getter;

@Getter
public class BlogCommentLikeResponseDto {
    Long id;
    Long userId;
    Long blogId;
    Long commentId;
    public BlogCommentLikeResponseDto(BlogCommentLike blogCommentLike){
        this.id = blogCommentLike.getId();
        this.userId = blogCommentLike.getUser().getId();
        this.blogId = blogCommentLike.getBlog().getId();
    }


}
