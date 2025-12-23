package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "google_account")
public class GoogleAccount {
    @Id
    private String id;

    private String name;

    private String surname;

    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

}
