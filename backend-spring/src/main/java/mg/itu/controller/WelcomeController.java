package mg.itu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, String> getWelcomeMessage() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to our Spring Boot application!");
        return response;
    }
}