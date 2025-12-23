package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddictionDTO {
    private UUID id;
    private String name;
    private String icon;
}
