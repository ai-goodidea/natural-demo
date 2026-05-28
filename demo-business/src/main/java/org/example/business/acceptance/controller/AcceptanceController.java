package org.example.business.acceptance.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.business.acceptance.dto.AcceptanceHeadVO;
import org.example.business.acceptance.dto.AcceptanceQuery;
import org.example.business.acceptance.dto.AcceptanceSaveRequest;
import org.example.business.acceptance.dto.AcceptanceVO;
import org.example.business.acceptance.dto.OperationLogVO;
import org.example.business.acceptance.dto.StatusChangeRequest;
import org.example.business.acceptance.service.AcceptanceExportService;
import org.example.business.acceptance.service.AcceptanceService;
import org.example.business.acceptance.service.OperationLogService;
import org.example.common.dto.PageResult;
import org.example.common.result.Result;
import org.example.feign.dto.AcceptanceSummaryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/business/acceptance")
@RequiredArgsConstructor
public class AcceptanceController {

    private final AcceptanceService acceptanceService;
    private final AcceptanceExportService exportService;
    private final OperationLogService operationLogService;

    @GetMapping("/page")
    public Result<PageResult<AcceptanceHeadVO>> page(AcceptanceQuery q) {
        return Result.success(acceptanceService.page(q));
    }

    @GetMapping("/{id}")
    public Result<AcceptanceVO> detail(@PathVariable Long id) {
        return Result.success(acceptanceService.detail(id));
    }

    @PostMapping
    public Result<AcceptanceVO> create(@Valid @RequestBody AcceptanceSaveRequest req) {
        req.setId(null);
        return Result.success(acceptanceService.save(req));
    }

    @PutMapping
    public Result<AcceptanceVO> update(@Valid @RequestBody AcceptanceSaveRequest req) {
        return Result.success(acceptanceService.save(req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        acceptanceService.delete(id);
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<AcceptanceVO> changeStatus(@PathVariable Long id,
                                             @Valid @RequestBody StatusChangeRequest req) {
        return Result.success(acceptanceService.changeStatus(id, req));
    }

    @PostMapping("/{id}/ai-summary")
    public Result<AcceptanceSummaryResult> aiSummary(@PathVariable Long id) {
        return Result.success(acceptanceService.aiSummary(id));
    }

    @PostMapping("/{id}/save-summary")
    public Result<AcceptanceVO> saveSummary(@PathVariable Long id,
                                            @RequestBody AcceptanceSummaryResult summary) {
        return Result.success(acceptanceService.saveSummary(id, summary));
    }

    @GetMapping("/{id}/logs")
    public Result<List<OperationLogVO>> logs(@PathVariable Long id) {
        return Result.success(operationLogService.listByTarget("ACCEPTANCE", String.valueOf(id)));
    }

    @GetMapping(value = "/export", produces = "application/octet-stream")
    public void export(HttpServletResponse resp) throws IOException {
        String filename = URLEncoder.encode("验收记录_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8);
        resp.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        resp.setHeader("Content-Disposition", "attachment; filename=" + filename);
        exportService.export(resp.getOutputStream());
        resp.flushBuffer();
    }
}
