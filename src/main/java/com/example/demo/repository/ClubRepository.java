package com.example.demo.repository;

import com.example.demo.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByType(Club.ClubType type);
    Page<Club> findByType(Club.ClubType type, Pageable pageable);

    List<Club> findByTypeAndTagsContaining(Club.ClubType type, String tag);

    @Query("SELECT c FROM Club c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Club> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM Club c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    Page<Club> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Club c WHERE c.type = :type AND :tag MEMBER OF c.tags")
    List<Club> findByTypeAndTag(@Param("type") Club.ClubType type, @Param("tag") String tag);
    
    @Query("SELECT c FROM Club c WHERE c.type = :type AND :tag MEMBER OF c.tags")
    Page<Club> findByTypeAndTag(@Param("type") Club.ClubType type, @Param("tag") String tag, Pageable pageable);
    
    Page<Club> findAll(Pageable pageable);
    
    boolean existsByName(String name);
}

