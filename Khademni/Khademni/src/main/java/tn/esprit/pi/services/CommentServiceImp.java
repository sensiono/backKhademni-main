package tn.esprit.pi.services;

import tn.esprit.pi.entities.Blog;
import tn.esprit.pi.entities.Comment;
import tn.esprit.pi.repositories.BlogRepository;
import tn.esprit.pi.repositories.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class CommentServiceImp implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BlogRepository blogRepository;

    public Comment createComment(Long blogId, String postedBy, String content) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isPresent()) {
            Comment comment = new Comment();
            comment.setBlog(optionalBlog.get());
            comment.setContent(content);
            comment.setPostedBy(postedBy);
            comment.setCreatedAt(new Date());
            return commentRepository.save(comment);

        }
        throw new EntityNotFoundException("Blog not found");
    }

    public List<Comment> getCommentsByBlogId(Long blogId){
        return commentRepository.findByBlogId(blogId);
    }

    public void deleteComment(Long blogId, Long commentId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (blogOptional.isPresent()) {
            Blog blog = blogOptional.get();
            Optional<Comment> commentOptional = commentRepository.findById(commentId);
            if (commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                if (comment.getBlog().getId().equals(blog.getId())) {
                    commentRepository.deleteById(commentId);
                    return; // Successfully deleted
                }
            }
        }
        throw new EntityNotFoundException("Comment or Blog not found or they are not associated");
    }


}

