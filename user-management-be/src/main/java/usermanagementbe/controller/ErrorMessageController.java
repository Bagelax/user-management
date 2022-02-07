package usermanagementbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usermanagementbe.model.User;
import usermanagementbe.service.ErrorMessageService;

@RestController
@RequestMapping("/errors")
public class ErrorMessageController {
    private final ErrorMessageService errorMessageService;

    public ErrorMessageController(ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("")
    public ResponseEntity<?> getErrorMessages() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(200).body(this.errorMessageService.findByUser(currentUser));
    }
}
