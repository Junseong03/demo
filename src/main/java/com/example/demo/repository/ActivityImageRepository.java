package com.example.demo.repository;

import com.example.demo.entity.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityImageRepository extends JpaRepository<ActivityImage, Long> {
    List<ActivityImage> findByActivityId(Long activityId);
    
    Optional<ActivityImage> findByIdAndActivityId(Long imageId, Long activityId);
    
    void deleteByActivityId(Long activityId);
}

