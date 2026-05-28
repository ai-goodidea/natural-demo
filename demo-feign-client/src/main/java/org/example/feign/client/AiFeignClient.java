package org.example.feign.client;

import org.example.common.result.Result;
import org.example.feign.dto.AcceptanceSummaryRequest;
import org.example.feign.dto.AcceptanceSummaryResult;
import org.example.feign.fallback.AiFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "boot-ai", fallback = AiFeignFallback.class, path = "/ai")
public interface AiFeignClient {

    @PostMapping("/summary/acceptance")
    Result<AcceptanceSummaryResult> summarizeAcceptance(@RequestBody AcceptanceSummaryRequest req);
}
