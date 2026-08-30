package com.riskboard.controller;

import com.riskboard.dto.RiskLimitDto;
import com.riskboard.dto.SectorExposureDto;
import com.riskboard.entity.LimitType;
import com.riskboard.service.DashBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/risk-limits")
public class DashBoardController {

    private final DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping
    public List<RiskLimitDto> getAll() {
        return dashBoardService.findAll();
    }

    @GetMapping("/sector-exposure")
    public List<SectorExposureDto> getSectorExposure(@RequestParam LimitType limitType) {
        return dashBoardService.getSectorExposure(limitType);
    }
}