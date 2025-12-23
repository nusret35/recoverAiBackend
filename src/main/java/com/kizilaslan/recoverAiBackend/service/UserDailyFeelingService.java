package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.model.Feeling;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserDailyFeeling;
import com.kizilaslan.recoverAiBackend.repository.UserDailyFeelingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDailyFeelingService {

    private final UserDailyFeelingRepository userDailyFeelingRepository;

    @Transactional
    public void create(Feeling feeling, LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserDailyFeeling userDailyFeeling = new UserDailyFeeling(user, feeling, date);
        userDailyFeelingRepository.save(userDailyFeeling);
    }

    public Optional<UserDailyFeeling> findByUserIdAndDate(LocalDateTime date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        return userDailyFeelingRepository.findByUserAndDate(user, date.toLocalDate());
    }

    @Transactional
    public void deleteByUserIdAndDate(LocalDateTime date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        userDailyFeelingRepository.deleteByUserAndDate(user, date.toLocalDate());
    }

}
