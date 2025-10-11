package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/math")
public class MathController {

    @GetMapping("/add")
    public String addNumbers(@RequestParam int a, @RequestParam int b) {
        int sum = a + b;
        return "Сумма чисел " + a + " и " + b + " = " + sum;
    }
}
