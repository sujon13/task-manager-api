package com.example.qa.service;

import com.example.qa.enums.TypeEnum;
import com.example.qa.model.Like;
import com.example.qa.model.LikeRequest;
import com.example.qa.repository.LikeRepository;
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
public class LikeService {
    private final LikeRepository likeRepository;

    private Like buildLike(LikeRequest request) {
        Like like = new Like();
        like.setType(TypeEnum.getByValue(request.getType()));
        like.setParentId(request.getParentId());
        return like;
    }

    @Transactional
    public Like createLike(LikeRequest request) {
        Like like = buildLike(request);
        try {
            likeRepository.save(like);
            return like;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Like> findById(int id) {
        return likeRepository.findById(id);
    }

    public List<Like> findByParentId(int parentId) {
        return likeRepository.findAllByParentId(parentId);
    }
}
