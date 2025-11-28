package com.example.demo.repository;

import com.example.demo.entity.TalentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalentProfileRepository extends JpaRepository<TalentProfile, Long> {
    @Query("SELECT t FROM TalentProfile t WHERE :skill MEMBER OF t.skills")
    List<TalentProfile> findBySkill(@Param("skill") String skill);
    
    Page<TalentProfile> findAll(Pageable pageable);
}

