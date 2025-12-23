package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_daily_feeling")
@NoArgsConstructor
public class UserDailyFeeling {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    private Feeling feeling;

    private LocalDate date;

    public UserDailyFeeling(AppUser user, Feeling feeling, LocalDate date) {
        this.user = user;
        this.feeling = feeling;
        this.date = date;
    }
}
