package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.exception.AddictionNotFoundException;
import com.kizilaslan.recoverAiBackend.model.Addiction;
import com.kizilaslan.recoverAiBackend.repository.AddictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddictionService {

    private final AddictionRepository addictionRepository;

    public List<Addiction> findAll() {
        return addictionRepository.findAll();
    }

    public Addiction findById(UUID id) {
        return addictionRepository.findById(id)
                .orElseThrow(() -> new AddictionNotFoundException("Addiction not found"));
    }

    public List<Addiction> getAddictionsCanBeAdded(List<Addiction> addictions) {
        return addictionRepository.getAddictionToBeAdded(addictions.stream().map(Addiction::getId).toList());
    }

}
