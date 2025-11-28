package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false)
    private String imageUrl; // MinIO URL

    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public void updateImageUrl(String imageUrl) {
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}

