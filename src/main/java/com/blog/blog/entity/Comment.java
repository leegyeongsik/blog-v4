package com.blog.blog.entity;

import com.blog.blog.dto.BlogCommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Comment")

public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment" , nullable = false)
    private String comment;
    @Column(name = "likes")
    private Long likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;
    public Comment(User user, Blog blog , String comment) {
        this.user = user;
        this.blog = blog;
        this.comment = comment;
        this.likes = 0L;
    }


    public void update(BlogCommentRequestDto requestDto) {
        this.comment = requestDto.getComment();
    }

    public void CommentLikeUpdate(){
        this.likes +=1L;
    }
    public void CommentLikeCancelUpdate(){
        this.likes -=1L;
    }
}
