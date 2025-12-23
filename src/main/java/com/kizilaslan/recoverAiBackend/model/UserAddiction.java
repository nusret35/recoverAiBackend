package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "user_addiction")
public class UserAddiction {

        @EmbeddedId
        private UserAddictionId id;

        private LocalDateTime startDate;

        private LocalDateTime lastRelapseDate;

        private String aiNote;

        @MapsId("user")
        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonBackReference
        @OnDelete(action = OnDeleteAction.CASCADE)
        private AppUser user;

        @ManyToOne
        @MapsId("addiction")
        @JoinColumn(name = "addiction_id")
        private Addiction addiction;

        @ManyToMany
        @JoinTable(name = "user_addiction_sobriety_achievement", joinColumns = {
                        @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
                        @JoinColumn(name = "addiction_id", referencedColumnName = "addiction_id")
        }, inverseJoinColumns = @JoinColumn(name = "sobriety_achievement_id", referencedColumnName = "id"))
        private List<SobrietyAchievement> achievements;

        @ManyToOne
        @JoinColumn(name = "next_achievement_id")
        private SobrietyAchievement nextAchievement;

        @CreationTimestamp
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;
}