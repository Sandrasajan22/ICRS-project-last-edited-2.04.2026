package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.PeopleSearchResponse;
import com.main.icrsbackend.service.SearchPeople;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private final SearchPeople searchPeople;

    // 🔍 GET /api/search/people?q=abc
    @GetMapping("/people")
    public ResponseEntity<List<PeopleSearchResponse>> searchPeople(
            @RequestParam(required = false) String q
    ) {

        // ✅ handle empty query (important)
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // ❌ no need meId anymore
        List<PeopleSearchResponse> result = searchPeople.searchPeople(null, q);

        return ResponseEntity.ok(result);
    }
}