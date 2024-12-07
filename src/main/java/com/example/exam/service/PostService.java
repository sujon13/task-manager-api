package com.example.exam.service;

import com.example.util.UserUtil;
import com.example.exam.entity.Post;
import com.example.exam.model.PostRequest;
import com.example.exam.repository.PostRepository;
import com.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserUtil userUtil;

    private Post buildPost(PostRequest postRequest) {
        Post post = new Post();
        BeanUtils.copyProperties(postRequest, post);
        return post;
    }

    @Transactional
    public Post addPost(PostRequest postRequest) {
        Post post = buildPost(postRequest);
        return postRepository.save(post);
    }

    Optional<Post> findById(final Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return postRepository.findById(id);
    }

    public Post getPost(final int id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id " + id));
    }

    public List<Post> findAllByIds(final Collection<Integer> ids) {
        return postRepository.findAllById(ids);
    }

    public Page<Post> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    private void editPost(Post post, PostRequest postRequest) {
        if (postRequest.getEngName() != null) {
            post.setEngName(postRequest.getEngName());
        }
        if (postRequest.getBngName() != null) {
            post.setBngName(postRequest.getBngName());
        }
        if (postRequest.getDescription() != null) {
            post.setDescription(postRequest.getDescription());
        }
        if (postRequest.getGrade() != null) {
            post.setGrade(postRequest.getGrade());
        }
    }

    @Transactional
    public Post editPost(final int id, PostRequest postRequest) {
        Post post = getPost(id);
        if (!userUtil.hasEditPermission(post)) {
            throw new AccessDeniedException("You do not have permission to edit this Post");
        }
        editPost(post, postRequest);
        return post;
    }

    @Transactional
    public void deletePost(final int id) {
        postRepository.deleteById(id);
    }
}
