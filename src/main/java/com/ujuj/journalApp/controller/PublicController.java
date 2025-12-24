package com.ujuj.journalApp.controller;


import com.ujuj.journalApp.entity.User;
import com.ujuj.journalApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {


    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public String healthcheck()
    {
        return "ok";
    }

    @PostMapping("/create-user")
    public void createUser(@RequestBody User user)
    {
        userService.saveEntry(user);
    }
}
