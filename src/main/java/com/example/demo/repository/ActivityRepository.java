package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByType(Activity.ActivityType type);
    Page<Activity> findByType(Activity.ActivityType type, Pageable pageable);

    List<Activity> findByTypeAndCategory(Activity.ActivityType type, Activity.ActivityCategory category);
    Page<Activity> findByTypeAndCategory(Activity.ActivityType type, Activity.ActivityCategory category, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.type = :type AND a.deadline >= :from AND a.deadline <= :to ORDER BY a.deadline ASC")
    List<Activity> findByTypeAndDateRange(@Param("type") Activity.ActivityType type, 
                                          @Param("from") LocalDate from, 
                                          @Param("to") LocalDate to);

    @Query("SELECT a FROM Activity a WHERE a.deadline >= :from AND a.deadline <= :to ORDER BY a.deadline ASC")
    List<Activity> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
    
    @Query("SELECT a FROM Activity a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<Activity> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT a FROM Activity a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    Page<Activity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Activity a WHERE a.type = :type AND (a.title LIKE %:keyword% OR a.description LIKE %:keyword%)")
    Page<Activity> findByTypeAndKeyword(@Param("type") Activity.ActivityType type, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Activity a WHERE a.type = :type AND a.category = :category AND (a.title LIKE %:keyword% OR a.description LIKE %:keyword%)")
    Page<Activity> findByTypeAndCategoryAndKeyword(@Param("type") Activity.ActivityType type, @Param("category") Activity.ActivityCategory category, @Param("keyword") String keyword, Pageable pageable);
    
    Page<Activity> findAll(Pageable pageable);
    
    // 동아리별 활동 조회
    List<Activity> findByClubId(Long clubId);
    Page<Activity> findByClubId(Long clubId, Pageable pageable);
}

