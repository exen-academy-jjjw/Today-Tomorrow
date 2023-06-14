package com.ezen.jjjw.repository;



import com.ezen.jjjw.domain.entity.BkBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BkBoardRepository extends JpaRepository<BkBoard, Long> {


        List<BkBoard> findAllByMemberId(Long memberId);
        List<BkBoard> findAllByMemberIdAndCategory(Long memberId, String category);

}
