package org.example.business.acceptance.controller;

import lombok.RequiredArgsConstructor;
import org.example.business.acceptance.dto.DeviceTypeStatsVO;
import org.example.business.acceptance.dto.SupplierStatsVO;
import org.example.business.acceptance.service.AcceptanceStatsService;
import org.example.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/business/stats")
@RequiredArgsConstructor
public class StatsController {

    private final AcceptanceStatsService statsService;

    @GetMapping("/supplier")
    public Result<List<SupplierStatsVO>> bySupplier() {
        return Result.success(statsService.bySupplier());
    }

    @GetMapping("/device-type")
    public Result<List<DeviceTypeStatsVO>> byDeviceType() {
        return Result.success(statsService.byDeviceType());
    }
}
