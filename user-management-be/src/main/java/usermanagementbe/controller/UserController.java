package usermanagementbe.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import usermanagementbe.model.Permission;
import usermanagementbe.model.Permissions;
import usermanagementbe.model.User;
import usermanagementbe.service.PermissionService;
import usermanagementbe.service.UserService;
import usermanagementbe.utils.ErrorResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
class MissingPermissionsErrorResponseBody extends ErrorResponseBody {
    private String missingPermission;

    public MissingPermissionsErrorResponseBody(String missingPermission) {
        super("Missing permission");
        this.setMissingPermission(missingPermission);
    }
}

@Data
@AllArgsConstructor
class NewUserRequestBody {
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    private UUID[] permissions;
}

@Data
@AllArgsConstructor
class UserDeletedResponseBody {
    @NonNull
    private UUID deletedId;
}

@RestController
@RequestMapping("/users")
public class UserController extends PermissionCheck {
    private final UserService userService;
    private final PermissionService permissionService;

    public UserController(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAll() {
        Permissions requiredPermission = Permissions.CAN_READ_USERS;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));
        return ResponseEntity.status(200).body(this.userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") UUID id) {
        Permissions requiredPermission = Permissions.CAN_READ_USERS;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));
        return ResponseEntity.status(200).body(this.userService.findById(id));
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> newUser(@RequestBody NewUserRequestBody body) {
        Permissions requiredPermission = Permissions.CAN_CREATE_USERS;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));
        Optional<User> existing = this.userService.findByEmail(body.getEmail());
        if (existing.isPresent())
            return ResponseEntity.status(422).body(new ErrorResponseBody("Email already registered."));
        List<Permission> permissions = new ArrayList<>();
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (UUID pId : body.getPermissions()) {
            Optional<Permission> p = this.permissionService.findById(pId);
            if (!p.isPresent())
                return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Permission with id %s not found", pId)));
            if (!currentUser.getPermissions().contains(p.get()))
                return ResponseEntity.status(403).body(new ErrorResponseBody(String.format("Cannot assign permission with id %s because you don't have that permission", pId)));
            permissions.add(p.get());
        }
        User newUser = new User();
        newUser.setEmail(body.getEmail());
        newUser.setPassword(body.getPassword());
        newUser.setName(body.getName());
        newUser.setSurname(body.getSurname());
        newUser.setPermissions(permissions);
        this.userService.save(newUser);
        return ResponseEntity.status(202).body(newUser);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @RequestBody NewUserRequestBody body) {
        Permissions requiredPermission = Permissions.CAN_UPDATE_USERS;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));
        Optional<User> targetUser = this.userService.findById(id);
        if (!targetUser.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("User with id %s not found", id)));
        List<Permission> permissions = new ArrayList<>();
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (UUID pId : body.getPermissions()) {
            Optional<Permission> p = this.permissionService.findById(pId);
            if (!p.isPresent())
                return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Permission with id %s not found", pId)));
            if (!currentUser.getPermissions().contains(p.get()))
                return ResponseEntity.status(403).body(new ErrorResponseBody(String.format("Cannot assign permission with id %s because you don't have that permission", pId)));
            permissions.add(p.get());
        }
        targetUser.get().setEmail(body.getEmail());
        if (!body.getPassword().equals(""))
            targetUser.get().setPassword(body.getPassword());
        targetUser.get().setName(body.getName());
        targetUser.get().setSurname(body.getSurname());
        targetUser.get().setPermissions(permissions);
        this.userService.save(targetUser.get());
        return ResponseEntity.status(200).body(targetUser);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        Permissions requiredPermission = Permissions.CAN_DELETE_USERS;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<User> targetUser = this.userService.findById(id);
        if (!targetUser.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("User with id %s not found", id)));
        this.userService.deleteById(id);
        return ResponseEntity.status(200).body(new UserDeletedResponseBody(id));
    }
}
