package com.blog.blog.entity;

import com.blog.blog.dto.BlogRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "Blog")
@NoArgsConstructor
public class Blog  extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "username" , nullable = false)
    private String username;

    @Column(name = "content" , nullable = false)
    private String content;

    @Column(name = "password" , nullable = false)
    private String password;

    @OneToMany(mappedBy = "blog" ,fetch = FetchType.LAZY , orphanRemoval = true )
    private List<Comment> commentList = new ArrayList<>();





    public Blog(BlogRequestDto blogRequestDto , User user){
        this.title = blogRequestDto.getTitle();
        this.username = user.getUsername();
        this.content = blogRequestDto.getContent();
        this.password = user.getPassword();
    }
    public void update(BlogRequestDto blogRequestDto ){
        this.title = blogRequestDto.getTitle();
        this.content = blogRequestDto.getContent();
    }
}
