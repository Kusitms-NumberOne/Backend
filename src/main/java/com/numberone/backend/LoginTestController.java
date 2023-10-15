package com.numberone.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginTestController {
    @RequestMapping("/logintest")
    public String test(Authentication authentication){
        return "Hello "+authentication.getName();
    }
}