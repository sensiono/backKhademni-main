package com.example.khademni.controller;

import com.example.khademni.entity.Blog;
import com.example.khademni.repository.BlogRepository;
import com.example.khademni.service.BlogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
public class BlogController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogRepository blogRepository;

    @PostMapping
    public ResponseEntity<?> createBlog(@RequestBody Blog blog) {
        try {
            Blog createdBlog = blogService.saveBlog(blog);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(blogService.getAllBlogs());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("{blogId}")
    public ResponseEntity<?> getBlogById(@PathVariable Long blogId) {
        try {
            Blog blog = blogService.getBlogById(blogId);
            return ResponseEntity.ok(blog);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PutMapping("/{blogId}/like")
    public ResponseEntity<?> likeBlog(@PathVariable Long blogId) {
        try {
            blogService.likeBlog(blogId);
            return ResponseEntity.ok(new String[]{"Blog liked successfully."});
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchByName(@PathVariable String name) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(blogService.searchByName(name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long blogId) {
        try {
            blogService.deleteBlog(blogId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete blog: " + e.getMessage());
        }
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long blogId, @RequestBody Blog updatedBlog) {
        try {
            Blog blog = blogService.updateBlog(blogId, updatedBlog);
            return new ResponseEntity<>(blog, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/ranked")
    public ResponseEntity<List<Blog>> getRankedBlogPosts() {
        try {
            blogService.calculateRankings(); // Calculate rankings before retrieving ranked blog posts
            List<Blog> rankedBlogs = blogService.getAllBlogs();
            // Sort the ranked blogs based on their score in descending order
            rankedBlogs.sort(Comparator.comparingDouble(Blog::getScore).reversed());
            return ResponseEntity.ok(rankedBlogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
