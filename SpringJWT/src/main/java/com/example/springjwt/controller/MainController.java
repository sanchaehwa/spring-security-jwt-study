package com.example.springjwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainP() {
        return "main Controller";
    }
}
