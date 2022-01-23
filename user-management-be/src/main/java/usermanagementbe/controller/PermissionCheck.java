package usermanagementbe.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import usermanagementbe.model.Permission;
import usermanagementbe.model.Permissions;
import usermanagementbe.model.User;

public class PermissionCheck {
    boolean checkPermission(Permissions p) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (Permission up : currentUser.getPermissions())
            if (up.getName().equals(p.name())) return true;
        return false;
    }
}
