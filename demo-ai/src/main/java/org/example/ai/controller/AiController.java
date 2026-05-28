package org.example.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.result.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "AI 对话", description = "通用 LLM 对话接口")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final ChatClient chatClient;

    @Operation(summary = "同步对话")
    @GetMapping("/chat")
    public Result<String> chat(@Parameter(description = "用户提示词") @RequestParam String prompt) {
        String answer = chatClient.prompt().user(prompt).call().content();
        return Result.success(answer);
    }

    @Operation(summary = "SSE 流式对话")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Parameter(description = "用户提示词") @RequestParam String prompt) {
        return chatClient.prompt().user(prompt).stream().content();
    }
}
