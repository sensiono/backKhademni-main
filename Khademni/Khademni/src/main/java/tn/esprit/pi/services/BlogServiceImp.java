package tn.esprit.pi.services;

import tn.esprit.pi.entities.Blog;
import tn.esprit.pi.entities.Comment;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.repositories.BlogRepository;
import tn.esprit.pi.repositories.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class BlogServiceImp implements BlogService {
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private CommentRepository commentRepository;

    public Blog saveBlog(Blog blog) {
        blog.setLikeCount(0);
        blog.setViewCount(0);
        blog.setDate(new Date());
        return blogRepository.save(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();


    }

    public Blog getBlogById(Long blogId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            blog.setViewCount(blog.getViewCount() + 1);
            return blogRepository.save(blog);
        } else {
            throw new EntityNotFoundException("Blog not found");
        }

    }

    public void likeBlog(Long blogId){
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            blog.setLikeCount(blog.getLikeCount() + 1);
            blogRepository.save(blog);
        }else{
            throw new EntityNotFoundException("Blog not found: " + blogId);
        }

    }
    public List<Blog> searchByName(String name) {
        return blogRepository.findAllByNameContaining(name);
    }
    public void deleteBlog(Long blogId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            // Delete associated comments
            List<Comment> comments = blog.getComments();
            commentRepository.deleteAll(comments);
            // Delete the blog
            blogRepository.deleteById(blogId);
        } else {
            throw new EntityNotFoundException("Blog not found: " + blogId);
        }
    }
    public Blog updateBlog(Long blogId, Blog updatedBlog, User currentUser) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();

            // Check if the current user is authorized to update the blog
            // Allow if the current user is the owner or has the admin role
            if (!blog.getUser().getId().equals(currentUser.getId()) ) {
                throw new SecurityException("You are not authorized to update this blog");
            }

            blog.setName(updatedBlog.getName());
            blog.setContent(updatedBlog.getContent());
            blog.setImg(updatedBlog.getImg());
            blog.setTags(updatedBlog.getTags());
            // Update other fields as needed

            return blogRepository.save(blog);
        } else {
            throw new EntityNotFoundException("Blog not found: " + blogId);
        }
    }
    public void calculateRankings() {
        List<Blog> allBlogs = blogRepository.findAll();
        for (Blog blog : allBlogs) {
            double score = calculateScore(blog);
            blog.setScore(score);
            blogRepository.save(blog);
        }
    }

    private double calculateScore(Blog blog) {
        // Adjust the algorithm to calculate the score based on your requirements
        // For example, you can consider factors like views, likes, comments, etc.
        double viewsFactor = blog.getViewCount() * 0.3;
        double likesFactor = blog.getLikeCount() * 0.2;
        double commentsFactor = blog.getComments().size() * 0.5;

        return viewsFactor + likesFactor + commentsFactor;
    }
}
