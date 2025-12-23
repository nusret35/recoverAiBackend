package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.dto.AddictionDTO;
import com.kizilaslan.recoverAiBackend.model.Addiction;
import com.kizilaslan.recoverAiBackend.request.UpdateRecoveryAchievementRequest;
import com.kizilaslan.recoverAiBackend.service.AddictionService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/addiction")
public class AddictionController {

    private final AddictionService addictionService;
    private final ModelMapper modelMapper;

    public AddictionController(AddictionService addictionService, ModelMapper modelMapper) {
        this.addictionService = addictionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AddictionDTO>> getAll() {
        List<Addiction> addictions = addictionService.findAll();
        List<AddictionDTO> addictionDTOList = Arrays.asList(modelMapper.map(addictions, AddictionDTO[].class));
        return ResponseEntity.ok(addictionDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddictionDTO> getAddiction(@PathVariable("id") UUID id) {
        Addiction addiction = addictionService.findById(id);
        AddictionDTO addictionDTO = modelMapper.map(addiction, AddictionDTO.class);
        return ResponseEntity.ok(addictionDTO);
    }

}
