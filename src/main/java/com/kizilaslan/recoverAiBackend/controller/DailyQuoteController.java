package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.model.DailyQuote;
import com.kizilaslan.recoverAiBackend.repository.DailyQuoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping("/daily-quotes")
public class DailyQuoteController {

    private final DailyQuoteRepository dailyQuoteRepository;

    @GetMapping
    public ResponseEntity<DailyQuote> dailyQuotes() {
        LocalDate today = LocalDate.now();
        long quoteIndex = today.getDayOfMonth() % 30;
        return ResponseEntity.ok(dailyQuoteRepository.findById(quoteIndex));
    }


}
