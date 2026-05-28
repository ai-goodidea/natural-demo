package org.example.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.common.model.acceptance.dto.AcceptanceSummaryRequest;
import org.example.common.model.acceptance.vo.AcceptanceSummaryResult;
import org.example.common.result.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 把验收单原始记录交给大模型，提炼出结构化验收结论。
 * 通过 Spring AI 的 ChatClient 调用 OpenAI 兼容协议（默认走 DeepSeek，可在 application.yml 中替换）。
 */
@Slf4j
@Tag(name = "AI 验收摘要", description = "把验收单原始记录交给大模型，提炼出结构化验收结论")
@RestController
@RequestMapping("/ai/summary")
@RequiredArgsConstructor
public class AiSummaryController {

    private static final String SYSTEM_PROMPT = """
            你是一位天然气工程设备验收专家，需要根据验收记录提炼结构化验收结论。
            必须严格按 JSON 格式返回，禁止任何前后缀文字或 markdown 代码块。
            JSON 字段：
            {
              "deviceSummary":  "设备信息汇总（一句话，包含主要设备类型与数量）",
              "inspectionOverview": "验收项目概述（外观/铭牌/规格/数量/功能等检查点）",
              "issues": "发现的问题（如无问题填\"无\"）",
              "conclusion": "QUALIFIED|CONCESSION|RETURNED 三选一",
              "conclusionText": "对应中文：合格 / 让步接收 / 退货"
            }
            判断规则：
            - 若所有明细 result 全部为 QUALIFIED 且 issues 为空 → QUALIFIED
            - 若存在 CONCESSION 但无 RETURNED → CONCESSION
            - 若存在 RETURNED → RETURNED
            """;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Operation(summary = "生成验收摘要", description = "25 秒硬超时，超时返回 504")
    @PostMapping("/acceptance")
    public Result<AcceptanceSummaryResult> summarize(@RequestBody AcceptanceSummaryRequest req) {
        String userPrompt = buildUserPrompt(req);
        log.info("AI summarize start, orderNo={}, items={}", req.getOrderNo(),
                req.getItems() == null ? 0 : req.getItems().size());

        // 调用大模型并设置 25 秒硬超时
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
                chatClient.prompt()
                        .system(SYSTEM_PROMPT)
                        .user(userPrompt)
                        .call()
                        .content());

        String raw;
        try {
            raw = future.get(25, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new BusinessException(504, "AI 服务超时，请稍后重试");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(500, "AI 调用被中断");
        } catch (ExecutionException e) {
            log.error("AI summarize failed", e);
            throw new BusinessException(500, "AI 服务调用失败：" + Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(e.getMessage()));
        }

        AcceptanceSummaryResult parsed = parseJson(raw);
        return Result.success(parsed);
    }

    private String buildUserPrompt(AcceptanceSummaryRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("【采购订单号】").append(safe(req.getOrderNo())).append('\n')
                .append("【供应商】").append(safe(req.getSupplierName())).append('\n')
                .append("【到货日期】").append(safe(req.getArrivalDate())).append('\n')
                .append("【验收人填写的记录】").append(safe(req.getRemark())).append('\n')
                .append("【明细行】\n");
        if (req.getItems() != null) {
            int i = 1;
            for (AcceptanceSummaryRequest.Item it : req.getItems()) {
                sb.append(i++).append(") ")
                        .append("编码=").append(safe(it.getDeviceCode()))
                        .append("，名称=").append(safe(it.getDeviceName()))
                        .append("，规格=").append(safe(it.getSpec()))
                        .append("，数量=").append(safe(it.getQty())).append(safe(it.getUnit()))
                        .append("，结果=").append(safe(it.getResult()))
                        .append("，缺陷=").append(safe(it.getDefectDesc()))
                        .append('\n');
            }
        }
        sb.append("请按系统提示词的 JSON 格式返回结论。");
        return sb.toString();
    }

    private AcceptanceSummaryResult parseJson(String raw) {
        if (raw == null) throw new BusinessException(500, "AI 返回为空");
        String cleaned = raw.trim();
        // 兜底剥离 ```json ... ``` 代码块
        if (cleaned.startsWith("```")) {
            int firstNl = cleaned.indexOf('\n');
            if (firstNl > 0) cleaned = cleaned.substring(firstNl + 1);
            if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
            cleaned = cleaned.trim();
        }
        try {
            return objectMapper.readValue(cleaned, AcceptanceSummaryResult.class);
        } catch (Exception e) {
            log.warn("parse AI json failed, raw=[{}]", raw, e);
            // 解析失败时把原文丢到 inspectionOverview，前端可继续编辑
            return AcceptanceSummaryResult.builder()
                    .deviceSummary("")
                    .inspectionOverview(raw)
                    .issues("AI 返回格式异常，已原文保留")
                    .conclusion("CONCESSION")
                    .conclusionText("让步接收")
                    .build();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
