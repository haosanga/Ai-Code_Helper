package com.haosanga.aicodehelper.controller;

import com.haosanga.aicodehelper.ai.AiCodeHelper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ChatController {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @PostMapping("/api/chat")
    public Map<String, Object> chat(@RequestParam String mode, @RequestParam String message) {
        try {
            String response = aiCodeHelper.chat(mode, message);
            return Map.of("success", true, "data", response);
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok");
    }
}
