package com.ezen.jjjw.repository;



import com.ezen.jjjw.domain.entity.BkBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BkBoardRepository extends JpaRepository<BkBoard, Long> {



}
