package com.blog.blog.Repository;

import com.blog.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogCommentRepository extends JpaRepository<Comment , Long> {
    List<Comment> findAllByOrderByModifiedAtDesc();


    Optional<Object> findByIdAndBlogId(Long blogId, Long id);

    Optional<Comment> findByIdAndBlog_Id(Long blogId , Long id);
}
