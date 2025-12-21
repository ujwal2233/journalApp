package com.ujuj.journalApp.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Healthcheck {
    @GetMapping("/healthcheck1")
    public String healthcheck()
    {
        return "ok";
    }
}
