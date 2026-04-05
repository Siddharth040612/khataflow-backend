package com.khataflow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/")
    public String home() {
        return "KhataFlow Backend Running 🚀";
    }

    @GetMapping("/test")
    public String test() {
        return "API Working ✅ NOW";
    }
}