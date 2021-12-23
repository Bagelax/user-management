package usermanagementbe.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import usermanagementbe.model.User;
import usermanagementbe.service.UserService;
import usermanagementbe.utils.ErrorResponseBody;
import usermanagementbe.utils.JwtUtil;

import java.util.Optional;

@Data
class LoginRequestBody {
    private String email;
    private String password;
}

@Data
@AllArgsConstructor
class LoginResponseBody {
    private String token;
}

@RestController
public class Login {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public Login(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequestBody loginRequestBody) {
        String email = loginRequestBody.getEmail();
        String password = loginRequestBody.getPassword();

        Optional<User> user = this.userService.findByEmail(email);
        if (!user.isPresent())
            return ResponseEntity.status(401).body(new ErrorResponseBody("User with provided email not found"));
        if (!user.get().checkPassword(password))
            return ResponseEntity.status(401).body(new ErrorResponseBody("Invalid password"));

        return ResponseEntity.status(200).body(new LoginResponseBody(this.jwtUtil.generateToken(email)));
    }
}
