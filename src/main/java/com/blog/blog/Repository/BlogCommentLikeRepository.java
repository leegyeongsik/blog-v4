package com.blog.blog.Repository;

import com.blog.blog.entity.Blog;
import com.blog.blog.entity.BlogCommentLike;
import com.blog.blog.entity.Comment;
import com.blog.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogCommentLikeRepository  extends JpaRepository<BlogCommentLike , Long> {

    Optional<BlogCommentLike> findByUserAndBlog(User user, Blog blog);
    Optional<BlogCommentLike> findByUserAndBlogAndComment_idIsNull(User user, Blog blog);
    Optional<BlogCommentLike> findByUserAndBlogAndComment(User user, Blog blog, Comment comment);
}
