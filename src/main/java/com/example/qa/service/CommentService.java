package com.example.qa.service;

import com.example.exception.NotFoundException;
import com.example.qa.enums.TypeEnum;
import com.example.qa.model.Comment;
import com.example.qa.model.CommentRequest;
import com.example.qa.model.Like;
import com.example.qa.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final LikeService likeService;

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

    public Comment getComment(int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id + " + id));
    }

    public Page<Comment> findByQuestionId(int questionId, Pageable pageable) {
        return commentRepository.findAllByQuestionId(questionId, pageable);
    }

    private Comment editLikeCount(int id, boolean increment) {
        Comment comment = getComment(id);
        comment.setLikeCount(comment.getLikeCount() + (increment ? 1 : -1));
        return comment;
    }

    private Comment incrementLikeCount(int id) {
        return editLikeCount(id, true);
    }

    @Transactional
    public Comment decrementLikeCount(int id) {
        return editLikeCount(id, false);
    }

    @Transactional
    public Optional<Comment> likeComment(int id) {

        Optional<Like> optionalLike = likeService.createLike(TypeEnum.COMMENT, id);
        if (optionalLike.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(incrementLikeCount(id));
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
