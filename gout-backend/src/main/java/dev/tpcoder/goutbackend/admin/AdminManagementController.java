package dev.tpcoder.goutbackend.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminManagementController {

    @GetMapping
    public String helloAdmin() {
        return "Hello Admin";
    }
}
