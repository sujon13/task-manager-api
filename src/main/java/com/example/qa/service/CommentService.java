package com.example.qa.service;

import com.example.qa.model.Comment;
import com.example.qa.model.CommentRequest;
import com.example.qa.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;

    private Comment buildComment(CommentRequest request) {
        Comment comment = new Comment();
        comment.setQuestionId(request.getQuestionId());
        comment.setDescription(request.getDescription());
        return comment;
    }

    @Transactional
    public Comment createComment(CommentRequest request) {
        Comment comment = buildComment(request);
        try {
            commentRepository.save(comment);
            return comment;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Comment> findById(int id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findByQuestionId(int questionId) {
        return commentRepository.findAllByQuestionId(questionId);
    }

    @Transactional
    public Comment editComment(CommentRequest request) {
        Optional<Comment> optionalComment = findById(request.getId());
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found with id + " + request.getId());
        }

        Comment comment = optionalComment.get();
        if (request.getDescription() != null)
            comment.setDescription(request.getDescription());

        return comment;
    }

    private Comment editLikeCount(int id, boolean increment) {
        Optional<Comment> optionalComment = findById(id);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found with id + " + id);
        }

        optionalComment.get().setLikeCount(optionalComment.get().getLikeCount() + (increment ? 1 : -1));
        return optionalComment.get();
    }

    @Transactional
    public Comment incrementLikeCount(int id) {
        return editLikeCount(id, true);
    }

    @Transactional
    public Comment decrementLikeCount(int id) {
        return editLikeCount(id, false);
    }

    @Transactional
    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public void deleteByQuestionId(int questionId) {
        commentRepository.deleteAllByQuestionId(questionId);
    }
}
