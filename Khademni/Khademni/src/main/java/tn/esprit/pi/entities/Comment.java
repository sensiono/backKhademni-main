package com.example.khademni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    private Date createdAt;
    private String postedBy;
    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;
}