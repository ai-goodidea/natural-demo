package org.example.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.feign.client.AiFeignClient;
import org.example.feign.dto.AcceptanceSummaryRequest;
import org.example.feign.dto.AcceptanceSummaryResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiFeignFallback implements AiFeignClient {

    @Override
    public Result<AcceptanceSummaryResult> summarizeAcceptance(AcceptanceSummaryRequest req) {
        log.warn("AiFeignClient.summarizeAcceptance fallback, orderNo={}", req == null ? null : req.getOrderNo());
        return Result.failed(500, "AI 服务暂不可用，请稍后重试");
    }
}
