package com.ezen.jjjw.repository;

import com.ezen.jjjw.domain.entity.ReviewFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FileRepository extends JpaRepository<ReviewFile, Long> {
    List<ReviewFile> findByReviewIdOrderByModifiedAtDesc(Long reviewId);
    List<ReviewFile> findAllByReviewId(Long id);
}

