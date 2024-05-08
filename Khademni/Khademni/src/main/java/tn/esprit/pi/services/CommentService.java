package tn.esprit.pi.services;

import tn.esprit.pi.entities.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Long blogId, String postedBy, String content);
    List<Comment> getCommentsByBlogId(Long blogId);

    void deleteComment(Long blogId, Long commentId);

}