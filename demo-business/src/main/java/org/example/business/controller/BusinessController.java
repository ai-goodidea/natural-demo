package org.example.business.controller;

import org.example.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("ok");
    }
}
