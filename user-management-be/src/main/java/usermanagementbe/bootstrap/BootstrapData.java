package usermanagementbe.bootstrap;

import usermanagementbe.model.Permission;
import usermanagementbe.model.Permissions;
import usermanagementbe.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import usermanagementbe.repository.PermissionRepository;
import usermanagementbe.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        List<Permission> permissions = new ArrayList<>();
        for (Permissions permission : Permissions.values()) {
            Permission p = new Permission();
            p.setName(permission.name());
            permissions.add(p);
        }

        System.out.println(this.permissionRepository.saveAll(permissions));

        User admin = new User();
        admin.setEmail("milutin@mail.com");
        admin.setName("Milutin");
        admin.setSurname("Milicevic");
        admin.setPassword("password");
        admin.setPermissions(permissions);

        System.out.println(this.userRepository.save(admin));

        User pera = new User();
        pera.setEmail("pera@peric.com");
        pera.setName("Pera");
        pera.setSurname("Peric");
        pera.setPassword("password");
        permissions.remove(1);
        permissions.remove(1);
        pera.setPermissions(permissions);

        System.out.println(this.userRepository.save(pera));

    }
}
