package com.sample.minimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GreeterService {
    private static final Logger log = LoggerFactory.getLogger(GreeterService.class);
    public String greet(String name) {
        String msg = "Hello, " + (name == null || name.isBlank() ? "world" : name) + "!";
        log.info("GreeterService producing message: {}", msg);
        return msg;
    }
}
