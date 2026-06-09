package com.example.orderservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "message",
                "Order Service Running Successfully"
        );

        return "index";
    }
    @GetMapping("/api/test")
    @ResponseBody
    public String test() {
        return "API OK";
    }


}