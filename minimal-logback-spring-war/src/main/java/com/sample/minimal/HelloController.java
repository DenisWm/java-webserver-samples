package com.sample.minimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    private final GreeterService greeter;

    public HelloController(GreeterService greeter) {
        this.greeter = greeter;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", required = false) String name) {
        String msg = greeter.greet(name);
        log.info("HelloController returning: {}", msg);
        return msg;
    }
}
