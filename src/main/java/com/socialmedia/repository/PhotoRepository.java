package com.socialmedia.repository;

import com.socialmedia.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Page<Photo> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Photo p JOIN p.tags t WHERE t.name = :tagName ORDER BY p.createdAt DESC")
    Page<Photo> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    Page<Photo> findByAuthorUsernameOrderByCreatedAtDesc(String username, Pageable pageable);
}
