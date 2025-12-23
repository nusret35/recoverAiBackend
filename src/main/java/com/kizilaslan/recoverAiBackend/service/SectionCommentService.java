package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.model.Section;
import com.kizilaslan.recoverAiBackend.model.SectionComment;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.repository.SectionCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SectionCommentService {

    private final SectionCommentRepository sectionCommentRepository;

    @Transactional
    public void save(SectionComment sectionComment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<SectionComment> existingComment = sectionCommentRepository.findBySection(sectionComment.getSection(),
                user.getId());
        if (existingComment.isPresent()) {
            SectionComment newComment = existingComment.get();
            newComment.setSection(sectionComment.getSection());
            newComment.setUpdatedTime(LocalDateTime.now());
            sectionCommentRepository.save(newComment);
            return;
        }
        sectionCommentRepository.save(sectionComment);
    }

    @Transactional
    public void deleteGoalComment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        sectionCommentRepository.deleteBySectionAndUser(Section.GOAL, user.getId());
    }

    public Optional<SectionComment> getBySection(Section section) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        return sectionCommentRepository.findBySection(section, user.getId());
    }

}
