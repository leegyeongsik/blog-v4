package com.blog.blog.Service;

import com.blog.blog.Repository.BlogCommentRepository;
import com.blog.blog.Repository.BlogRepository;
import com.blog.blog.dto.*;
import com.blog.blog.entity.Blog;
import com.blog.blog.entity.Comment;
import com.blog.blog.entity.User;
import com.blog.blog.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    public BlogService(BlogRepository blogRepository, BlogCommentRepository blogCommentRepository){
        this.blogRepository = blogRepository;
        this.blogCommentRepository = blogCommentRepository;
    }
    public BlogResponseDto createBlog( User user ,BlogRequestDto blogRequestDto) {
        Blog blog = new Blog(blogRequestDto , user);
        Blog saveblog = blogRepository.save(blog);
        return new BlogResponseDto(saveblog);
    }
    public List<BlogResponseDto> getBlogs() {
        List<BlogResponseDto> blogResponseDtoList =  new ArrayList<>();
        List<Blog> blogList =  blogRepository.findAllByOrderByModifiedAtDesc();

        for (Blog blog : blogList) {
            List<CommentResponseDto> commentList = new ArrayList<>();
            if (!blog.getCommentList().isEmpty()) {
                for (Comment comment : blog.getCommentList()) {
                    commentList.add(new CommentResponseDto(comment));
                }
            }
            blogResponseDtoList.add(new BlogResponseDto(blog, commentList));
        }
        return blogResponseDtoList;
    }

    public BlogResponseDto selectedPost(Long id) {
        Blog blog = findName(id);
        List<CommentResponseDto> commentList = new ArrayList<>();
        if (!blog.getCommentList().isEmpty()) {
            for (Comment comment : blog.getCommentList()) {
                commentList.add(new CommentResponseDto(comment));
            }
        }
        return new BlogResponseDto(blog, commentList);
    }
    @Transactional
    public Blog updatePost(BlogRequestDto blogRequestDto , Long id , User user, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        confirmTokenId(blog  , user , response);
        blog.update(blogRequestDto);
        return blog;

    }


    public String deletePost(Long id , User user, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        confirmTokenId(blog  , user ,response);
        blogRepository.delete(blog);
        return "삭제 성공";
    }

    public BlogCommentResponseDto createBlogComment(Long id, BlogCommentRequestDto requestDto, User user) {
        Blog blog = findName(id);
        Comment comment = new Comment(user,blog,requestDto.getComment());
        Comment savecomment= blogCommentRepository.save(comment);

        return new BlogCommentResponseDto(savecomment.getComment());
    }
    @Transactional
    public Blog updateBlogComment(Long id, BlogCommentRequestDto requestDto, User user, Long blogId, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        Comment comment =findComment(id,blogId);
        confirmTokenUserAndComment(comment,user ,response );
        comment.update(requestDto);
        return blog;
    }
    public List<CommentResponseDto> updateComment(Blog blog) {
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment comments : blog.getCommentList()) {
            commentList.add(new CommentResponseDto(comments));
            }
        return commentList;
    }

    public String deleteBlogComment(Long id , User user, Long blogId, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        Comment comment =findComment(id,blogId);
        confirmTokenUserAndComment(comment,user,response);
        blogCommentRepository.delete(comment);
        return "삭제완료";
    }



    private Blog findName(Long id) {
        return blogRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다")
        );
    }
    private void confirmTokenId(Blog blog , User user , HttpServletResponse response) throws IOException {
        if (blog.getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ADMIN)){
            System.out.println("작성한 글이 맞습니다");
        } else {
            response.setStatus(400);
            new ObjectMapper().writeValue(response.getOutputStream(), "해당 유저가 작성한글이 아닙니다"+"status:"+response.getStatus());
            throw new IllegalArgumentException("해당 유저가 작성한글이 아닙니다");
        }
    }

    private Comment findComment(Long id,Long blogId) {
        Optional<Comment> objectOptional = blogCommentRepository.findByIdAndBlog_Id(blogId, id);
        if (!objectOptional.isPresent()) {
            throw new IllegalArgumentException("해당 게시판에 댓글이 존재하지 않습니다");
        }
        return blogCommentRepository.findById(blogId).orElseThrow(() -> new IllegalArgumentException("해당 게시판에 댓글이 존재하지 않습니다"));
    }
        private void confirmTokenUserAndComment(Comment comment , User user , HttpServletResponse response) throws IOException {
        if (comment.getUser().getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ADMIN)){
            System.out.println("작성한 댓글이 맞습니다");
        } else {
            response.setStatus(400);
            new ObjectMapper().writeValue(response.getOutputStream(), "해당 유저가 작성한 댓글이 아닙니다"+"status:"+response.getStatus());
            throw new IllegalArgumentException("해당 유저의 댓글이 아닙니다");
        }
    }



}
