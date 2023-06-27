package com.ezen.jjjw.repository;

import com.ezen.jjjw.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName    : com.RSMboard.RSMboard.repository
 * fileName       : ReviewRepository.java
 * author         : won
 * date           : 2023-06-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        won       최초 생성
 */

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
