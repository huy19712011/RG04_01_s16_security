package com.example.rg04_01_s16_security.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldResource {

    @GetMapping("/hello-world")
    public String helloWorld() {

        return "Hello World";
    }

}
