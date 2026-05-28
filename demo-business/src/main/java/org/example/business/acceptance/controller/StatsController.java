package org.example.business.acceptance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.business.acceptance.service.AcceptanceStatsService;
import org.example.common.model.acceptance.vo.DeviceTypeStatsVO;
import org.example.common.model.acceptance.vo.SupplierStatsVO;
import org.example.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "验收统计", description = "按供应商 / 设备类型聚合的验收统计")
@RestController
@RequestMapping("/business/stats")
@RequiredArgsConstructor
public class StatsController {

    private final AcceptanceStatsService statsService;

    @Operation(summary = "按供应商聚合统计")
    @GetMapping("/supplier")
    public Result<List<SupplierStatsVO>> bySupplier() {
        return Result.success(statsService.bySupplier());
    }

    @Operation(summary = "按设备类型聚合统计")
    @GetMapping("/device-type")
    public Result<List<DeviceTypeStatsVO>> byDeviceType() {
        return Result.success(statsService.byDeviceType());
    }
}
