package usermanagementbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Health {

    public Health() {}

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("Ok");
    }

}
