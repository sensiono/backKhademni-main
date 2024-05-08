package tn.esprit.pi.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import tn.esprit.pi.entities.Blog;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.repositories.BlogRepository;
import tn.esprit.pi.services.BlogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

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

    @RequestMapping(value = "/updateBlog/{blogId}", method = RequestMethod.PUT)
    public ResponseEntity<Blog> updateBlog(
            @PathVariable Long blogId,
            @RequestBody Blog updatedBlog,
            @AuthenticationPrincipal User currentUser // Inject the current authenticated user
    ) {
        try {
            Blog blog = blogService.updateBlog(blogId, updatedBlog, currentUser);
            return ResponseEntity.ok(blog);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return 403 Forbidden if unauthorized
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if blog not found
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
