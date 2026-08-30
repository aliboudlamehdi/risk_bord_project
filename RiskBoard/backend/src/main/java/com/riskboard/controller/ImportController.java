package com.riskboard.controller;

import com.riskboard.dto.ImportSummary;
import com.riskboard.service.CsvImportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final CsvImportService csvImportService;

    public ImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping(path = "/risk-limits", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportSummary importRiskLimits(@RequestParam("file") MultipartFile file) {
        try {
            return csvImportService.importRiskLimits(file.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read uploaded file", e);
        }
    }
}
