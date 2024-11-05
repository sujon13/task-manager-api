package com.example.qa.repository;

import com.example.qa.enums.TypeEnum;
import com.example.qa.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    List<Like> findAllByType(TypeEnum type);

    List<Like> findAllByParentId(Integer parentId);

    boolean existsByParentIdAndCreatedBy(Integer parentId, String createdBy);
}
