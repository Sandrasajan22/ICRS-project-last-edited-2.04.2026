package com.main.icrsbackend.controller;

import com.main.icrsbackend.service.AdminImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminImportController {

    private final AdminImportService adminImportService;

    @PostMapping("/{type}/upload")
    public ResponseEntity<String> upload(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file
    ) {
        String message = adminImportService.upload(type, file);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{type}/template")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String type) {
        byte[] data = adminImportService.generateTemplate(type);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}