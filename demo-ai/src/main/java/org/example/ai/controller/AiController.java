package org.example.ai.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.result.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final ChatClient chatClient;

    @GetMapping("/chat")
    public Result<String> chat(@RequestParam String prompt) {
        String answer = chatClient.prompt().user(prompt).call().content();
        return Result.success(answer);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String prompt) {
        return chatClient.prompt().user(prompt).stream().content();
    }
}
