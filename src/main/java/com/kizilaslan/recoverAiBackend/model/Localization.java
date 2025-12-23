package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;


@Data
@Table(name = "localization", uniqueConstraints = @UniqueConstraint(columnNames = {"locale", "translation_key"}))
@Entity
public class Localization {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Language locale;

    @Column(name = "localization_key", nullable = false)
    private String localizationKey;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;


}
