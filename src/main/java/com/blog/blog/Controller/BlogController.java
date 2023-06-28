package com.blog.blog.Controller;

import com.blog.blog.Service.BlogService;
import com.blog.blog.dto.BlogCommentRequestDto;
import com.blog.blog.dto.BlogCommentResponseDto;
import com.blog.blog.dto.BlogRequestDto;
import com.blog.blog.dto.BlogResponseDto;
import com.blog.blog.entity.Blog;
import com.blog.blog.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @PostMapping("/blogs") // 게시글 등록
    public BlogResponseDto createBlog(@AuthenticationPrincipal UserDetailsImpl userDetails ,
                                      @RequestBody BlogRequestDto requestDto){
        return blogService.createBlog(userDetails.getUser() , requestDto);
    }
    @GetMapping("/blogs") // 게시글 전체조회
    public List<BlogResponseDto> getBlogs(){
        return blogService.getBlogs();
    }
    @GetMapping("/blogs/{id}") // 특정 id 게시글 조회
    public BlogResponseDto selectedPost(@PathVariable Long id){
        return blogService.selectedPost(id);
    }
    @PutMapping("/blogs/{id}") // 특정 id 게시글 수정하는데 토큰 아이디랑 , username 이랑 같다면 수정
    public BlogResponseDto updatePost(@PathVariable Long id ,
                                      @RequestBody BlogRequestDto blogRequestDto ,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                      HttpServletResponse response) throws IOException {
        Blog blog = blogService.updatePost(blogRequestDto,id,userDetails.getUser() , response);
        return new BlogResponseDto(blog , blogService.updateComment(blog));
    }
    @DeleteMapping("/blogs/{id}") // 특정 id 게시글 수정하는데 토큰 아이디랑 , username 이랑 같다면 삭제
    public String deletePost(@PathVariable Long id  ,
                             @AuthenticationPrincipal UserDetailsImpl userDetails ,
                             HttpServletResponse response) throws IOException{
        return blogService.deletePost(id ,userDetails.getUser() ,response);
    }
    @PostMapping("/blogs/comments/{id}") // 댓글 등록
    public BlogCommentResponseDto createBlogComment(@PathVariable Long id ,
                                                    @RequestBody BlogCommentRequestDto requestDto ,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails
                                                    ){
        return blogService.createBlogComment(id , requestDto , userDetails.getUser());
    }
    @PutMapping("/blogs/{id}/comments/{blogId}") // 댓글 수정
    public BlogResponseDto updateBlogComment(@PathVariable Long id,
                                             @RequestBody BlogCommentRequestDto requestDto ,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails ,
                                             @PathVariable Long blogId,
                                             HttpServletResponse response) throws IOException {
        Blog blog =blogService.updateBlogComment(id , requestDto , userDetails.getUser() , blogId ,response ); // blogid -> comment id
        return new BlogResponseDto(blog , blogService.updateComment(blog));
    }
    @DeleteMapping("/blogs/{id}/comments/{blogId}")
    public String deleteBlogComment(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails ,
                                    @PathVariable Long blogId,
                                    HttpServletResponse response) throws IOException {
        return blogService.deleteBlogComment(id,userDetails.getUser(),blogId , response);
    }


}
