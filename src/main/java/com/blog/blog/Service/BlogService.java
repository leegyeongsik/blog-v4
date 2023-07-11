package com.blog.blog.Service;

import com.blog.blog.Repository.BlogCommentLikeRepository;
import com.blog.blog.Repository.BlogCommentRepository;
import com.blog.blog.Repository.BlogRepository;
import com.blog.blog.dto.*;
import com.blog.blog.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final BlogCommentLikeRepository blogCommentLikeRepository;

    public BlogResponseDto createBlog(User user, BlogRequestDto blogRequestDto) {
        Blog blog = new Blog(blogRequestDto, user);
        Blog saveblog = blogRepository.save(blog);
        return new BlogResponseDto(saveblog);
    }

    public List<BlogResponseDto> getBlogs() {
        List<BlogResponseDto> blogResponseDtoList = new ArrayList<>();
        List<Blog> blogList = blogRepository.findAllByOrderByModifiedAtDesc();

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

    public BlogResponseDto selectedPost(Long id, User user) {
        Blog blog = findName(id);
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment comment : blog.getCommentList()) {
            System.out.println(comment);
            Optional<BlogCommentLike> isExistBlogCommentLike = blogCommentLikeRepository.findByUserAndBlogAndComment(user,blog,comment); // 세개가 다 존재한다면 댓글좋아요
            if (isExistBlogCommentLike.isPresent()){
                commentList.add(new CommentResponseDto(comment,isExistBlogCommentLike));
            } else {
                commentList.add(new CommentResponseDto(comment,isExistBlogCommentLike));
            }
        }

        Optional<BlogCommentLike> isExistBlogLike = blogCommentLikeRepository.findByUserAndBlogAndComment_idIsNull(user, blog); // 2개가 존재하고 하나가 null 즉 게시글좋아요
        if (isExistBlogLike.isPresent()) { // 존재한다면 해당 게시판에 해당 로그인한 아이디가 좋아요를 누른적이 존재한다는것임
            new BlogCommentLikeResponseDto(isExistBlogLike.get());
            return new BlogResponseDto(blog, commentList, new BlogCommentLikeResponseDto(isExistBlogLike.get()));
        } else { // 없으면 누르지않았다는 것
            return new BlogResponseDto(blog, commentList, null);
        }
    }

    @Transactional
    public Blog updatePost(BlogRequestDto blogRequestDto, Long id, User user, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        confirmTokenId(blog, user, response);
        blog.update(blogRequestDto);
        return blog;
    }


    public String deletePost(Long id, User user, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        confirmTokenId(blog, user, response);
        blogRepository.delete(blog);
        return "삭제 성공";
    }

    public BlogCommentResponseDto createBlogComment(Long id, BlogCommentRequestDto requestDto, User user) {
        Blog blog = findName(id);
        Comment comment = new Comment(user, blog, requestDto.getComment());
        Comment savecomment = blogCommentRepository.save(comment);

        return new BlogCommentResponseDto(savecomment.getComment());
    }

    @Transactional
    public Blog updateBlogComment(Long id, BlogCommentRequestDto requestDto, User user, Long blogId, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        Comment comment = findComment(id, blogId);
        confirmTokenUserAndComment(comment, user, response);
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

    public String deleteBlogComment(Long id, User user, Long blogId, HttpServletResponse response) throws IOException {
        Blog blog = findName(id);
        Comment comment = findComment(id, blogId);
        confirmTokenUserAndComment(comment, user, response);
        blogCommentRepository.delete(comment);
        return "삭제완료";
    }


    private Blog findName(Long id) {
        return blogRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다")
        );
    }

    private void confirmTokenId(Blog blog, User user, HttpServletResponse response) throws IOException {
        if (blog.getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ADMIN)) {
            System.out.println("작성한 글이 맞습니다");
        } else {
            response.setStatus(400);
            new ObjectMapper().writeValue(response.getOutputStream(), "해당 유저가 작성한글이 아닙니다" + "status:" + response.getStatus());
            throw new IllegalArgumentException("해당 유저가 작성한글이 아닙니다");
        }
    }

    private Comment findComment(Long id, Long blogId) {
        Optional<Comment> objectOptional = blogCommentRepository.findByBlog_idAndId(id, blogId);
        if (!objectOptional.isPresent()) {
            throw new IllegalArgumentException("해당 게시판에 댓글이 존재하지 않습니다");
        }
        return blogCommentRepository.findById(blogId).orElseThrow(() -> new IllegalArgumentException("해당 게시판에 댓글이 존재하지 않습니다"));
    }

    private void confirmTokenUserAndComment(Comment comment, User user, HttpServletResponse response) throws IOException {
        if (comment.getUser().getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ADMIN)) {
            System.out.println("작성한 댓글이 맞습니다");
        } else {
            response.setStatus(400);
            new ObjectMapper().writeValue(response.getOutputStream(), "해당 유저가 작성한 댓글이 아닙니다" + "status:" + response.getStatus());
            throw new IllegalArgumentException("해당 유저의 댓글이 아닙니다");
        }
    }


    @Transactional
    public ApiResponseDto BlogLike(User user, Long blogId, BlogCommentLikeRequestDto blogCommentLikeRequestDto) {
        return isCancelOrSaveBlogLike(user, blogId, blogCommentLikeRequestDto);
    }

    @Transactional
    public ApiResponseDto BlogLikeCancel(User user, Long blogId, BlogCommentLikeRequestDto blogCommentLikeRequestDto) {
        return isCancelOrSaveBlogLike(user, blogId, blogCommentLikeRequestDto);
    }
    @Transactional
    public ApiResponseDto BlogCommentLike(User user, Long blogId, BlogCommentLikeRequestDto blogCommentLikeRequestDto, Long commentId) {
        return isCancelOrSaveBlogCommentLike(user,blogId,blogCommentLikeRequestDto,commentId);
    }
    @Transactional
    public ApiResponseDto BlogCommentLikeCancel(User user, Long blogId, BlogCommentLikeRequestDto blogCommentLikeRequestDto, Long commentId) {
        return isCancelOrSaveBlogCommentLike(user,blogId,blogCommentLikeRequestDto,commentId);
    }

    private ApiResponseDto isCancelOrSaveBlogLike(User user, Long blogId, BlogCommentLikeRequestDto blogCommentLikeRequestDto) {
        Blog blog = findName(blogId);
        Optional<BlogCommentLike> isExistBlogCommentLike = blogCommentLikeRepository.findByUserAndBlogAndComment_idIsNull(user, blog); // 2개가 존재하고 하나가 null 즉 게시글좋아요
        if (!blogCommentLikeRequestDto.getBlogLike()) { // 들어온값이 false일때                                                           // isnull뺏더니 user랑 blog랑 일치하는거 다갖고옴
            if (!blog.getUsername().equals(user.getUsername())) { // 블로그의 글쓴이랑 로그인한 유저의 이름이랑 다를때
                if (isExistBlogCommentLike.isEmpty()) { // 로우에 해당하는 테이블이 존재하지 않는다면
                    blog.likeUpdate(); // 좋아요 1증가
                    BlogCommentLike blogCommentLike = new BlogCommentLike(user, blog);
                    blogCommentLikeRepository.save(blogCommentLike); // 테이블에 좋아요 로우 추가
                    return new ApiResponseDto("좋아요", 200);
                }
            } else { // 글쓴이는 좋아요를 누를수없음
                throw new IllegalArgumentException("글쓴이는 좋아요를 누를수 없습니다");
            }
        } else { // 들어온값이 true일때
            if (!blog.getUsername().equals(user.getUsername())) { //블로그의 글쓴이랑 로그인한 유저의 이름이랑 다를때
                if (isExistBlogCommentLike.isPresent()) {// 로우에 해당하는 테이블이 존재한다면
                    blog.likeCancelUpdate(); // 해당 blog 좋아요 1감소
                    blogCommentLikeRepository.delete(isExistBlogCommentLike.get()); // 테이블의 로우 삭제
                    return new ApiResponseDto("좋아요 취소", 200);
                }
            } else { // 글쓴이는 좋아요를 누를수없음
                throw new IllegalArgumentException("글쓴이는 좋아요를 취소할 수없습니다");
            }
        }
        return null;
    }


    public ApiResponseDto isCancelOrSaveBlogCommentLike(User user, Long blogId, BlogCommentLikeRequestDto blogCommentLikeRequestDto, Long commentId) {
        Blog blog = findName(blogId);
        Comment comment = findComment(blogId, commentId);
        Optional<BlogCommentLike> isExistBlogCommentLike = blogCommentLikeRepository.findByUserAndBlogAndComment(user, blog, comment);

        if (blogCommentLikeRequestDto.getCommentLike()) { // 존
            if (!comment.getUser().getUsername().equals(user.getUsername())) {
                if (isExistBlogCommentLike.isPresent()) {
                    comment.CommentLikeCancelUpdate();
                    blogCommentLikeRepository.delete(isExistBlogCommentLike.get());
                    return new ApiResponseDto("댓글 좋아요 취소", 200);
                }
            } else { // 댓글쓴이는 좋아요를 누를수없음
                throw new IllegalArgumentException("글쓴이는 좋아요를 취소할 수없습니다");
            }
        } else {
            if (!comment.getUser().getUsername().equals(user.getUsername())) { // 로그인한 유저가 댓글을 쓴 유저랑 같지 않을때
                if (isExistBlogCommentLike.isEmpty()) { // 로그인한 유저가 해당 게시글의 코멘트에 좋아요를 누른 기록이 존재하지않다면
                    comment.CommentLikeUpdate(); // 좋아요 1 증가
                    BlogCommentLike blogCommentLike = new BlogCommentLike(user, blog, comment);
                    blogCommentLikeRepository.save(blogCommentLike);
                    return new ApiResponseDto("댓글 좋아요", 200);
                }
            } else { // 댓글쓴이는 좋아요를 누를수없음
                throw new IllegalArgumentException("글쓴이는 좋아요를 누를수 없습니다");
            }
        }
        return null;
    }
}

