package com.blog.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "BlogCommentLike")
@NoArgsConstructor

public class BlogCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "comment_id" )
    private Comment comment;

    public BlogCommentLike(User user, Blog blog){
        this.user = user;
        this.blog = blog;
    }
    public BlogCommentLike(User user , Blog blog , Comment comment){
    this.user = user;
    this.blog = blog;
    this.comment = comment;
    }

}
