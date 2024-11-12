package com.kbc.kibi_coins.repository;

import com.kbc.kibi_coins.model.CommentCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentCategoryMappingRepository extends JpaRepository<CommentCategoryMapping, Long> {
    Optional<CommentCategoryMapping> findByComment(String comment);
}
