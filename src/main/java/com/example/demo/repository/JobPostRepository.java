package com.example.demo.repository;

import com.example.demo.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("SELECT j FROM JobPost j WHERE j.major = :major OR :major MEMBER OF j.tags")
    List<JobPost> findByMajor(@Param("major") String major);
    
    Page<JobPost> findAll(Pageable pageable);
}

