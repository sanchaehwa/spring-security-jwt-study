package com.example.springjwt.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@ResponseBody
public class AdminController {

    @GetMapping("/admin")
    public String adminP() {

        return "admin Controller";
    }
}
