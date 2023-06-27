package com.ezen.jjjw.repository;



import com.ezen.jjjw.domain.entity.BkBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BkBoardRepository extends JpaRepository<BkBoard, Long> {

        Page<BkBoard> findAllByMemberId(Long memberId, Pageable pageable);
        Page<BkBoard> findAllByMemberIdAndCategory(Long memberId, String category, Pageable pageable);
        Integer countByMemberId(Long memberId);
        Integer countByMemberIdAndCompletion(Long memberId, int completionValue);

}
