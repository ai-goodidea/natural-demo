package org.example.business.acceptance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.business.acceptance.service.AcceptanceExportService;
import org.example.business.acceptance.service.AcceptanceService;
import org.example.business.acceptance.service.OperationLogService;
import org.example.common.dto.PageResult;
import org.example.common.model.acceptance.dto.AcceptanceQuery;
import org.example.common.model.acceptance.dto.AcceptanceSaveRequest;
import org.example.common.model.acceptance.dto.StatusChangeRequest;
import org.example.common.model.acceptance.vo.AcceptanceHeadVO;
import org.example.common.model.acceptance.vo.AcceptanceSummaryResult;
import org.example.common.model.acceptance.vo.AcceptanceVO;
import org.example.common.model.acceptance.vo.OperationLogVO;
import org.example.common.result.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Tag(name = "验收单管理", description = "验收单增删改查、状态流转、AI 摘要、导出")
@RestController
@RequestMapping("/business/acceptance")
@RequiredArgsConstructor
public class AcceptanceController {

    private final AcceptanceService acceptanceService;
    private final AcceptanceExportService exportService;
    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询验收单")
    @GetMapping("/page")
    public Result<PageResult<AcceptanceHeadVO>> page(AcceptanceQuery q) {
        return Result.success(acceptanceService.page(q));
    }

    @Operation(summary = "查询验收单详情（含明细 / 整改）")
    @GetMapping("/{id}")
    public Result<AcceptanceVO> detail(@Parameter(description = "验收单 ID") @PathVariable Long id) {
        return Result.success(acceptanceService.detail(id));
    }

    @Operation(summary = "新建验收单")
    @PostMapping
    public Result<AcceptanceVO> create(@Valid @RequestBody AcceptanceSaveRequest req) {
        req.setId(null);
        return Result.success(acceptanceService.save(req));
    }

    @Operation(summary = "更新验收单（明细全量替换）")
    @PutMapping
    public Result<AcceptanceVO> update(@Valid @RequestBody AcceptanceSaveRequest req) {
        return Result.success(acceptanceService.save(req));
    }

    @Operation(summary = "删除验收单（终态不可删）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "验收单 ID") @PathVariable Long id) {
        acceptanceService.delete(id);
        return Result.success();
    }

    @Operation(summary = "流转验收单状态")
    @PatchMapping("/{id}/status")
    public Result<AcceptanceVO> changeStatus(@Parameter(description = "验收单 ID") @PathVariable Long id,
                                             @Valid @RequestBody StatusChangeRequest req) {
        return Result.success(acceptanceService.changeStatus(id, req));
    }

    @Operation(summary = "调用 AI 生成验收结论")
    @PostMapping("/{id}/ai-summary")
    public Result<AcceptanceSummaryResult> aiSummary(@Parameter(description = "验收单 ID") @PathVariable Long id) {
        return Result.success(acceptanceService.aiSummary(id));
    }

    @Operation(summary = "保存（前端可修订后的）AI 验收结论")
    @PostMapping("/{id}/save-summary")
    public Result<AcceptanceVO> saveSummary(@Parameter(description = "验收单 ID") @PathVariable Long id,
                                            @RequestBody AcceptanceSummaryResult summary) {
        return Result.success(acceptanceService.saveSummary(id, summary));
    }

    @Operation(summary = "查询验收单操作日志")
    @GetMapping("/{id}/logs")
    public Result<List<OperationLogVO>> logs(@Parameter(description = "验收单 ID") @PathVariable Long id) {
        return Result.success(operationLogService.listByTarget("ACCEPTANCE", String.valueOf(id)));
    }

    @Operation(summary = "导出验收单 Excel",
            description = "导出两个 Sheet：头信息 + 明细行；支持按列表页同条件过滤")
    @GetMapping("/export")
    public void export(AcceptanceQuery query, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String filename = URLEncoder.encode("验收记录_" + System.currentTimeMillis() + ".xlsx",
                StandardCharsets.UTF_8).replace("+", "%20");
        resp.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + filename);
        try {
            exportService.export(query, resp.getOutputStream());
        } catch (Exception e) {
            log.error("export acceptance excel failed", e);
            resp.reset();
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.getWriter().write("{\"code\":500,\"message\":\"导出失败：" + e.getMessage() + "\"}");
        }
    }
}
