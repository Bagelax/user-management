package usermanagementbe.service;

import org.springframework.stereotype.Service;
import usermanagementbe.model.Permission;
import usermanagementbe.repository.PermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionService implements IService<Permission, UUID> {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission save(Permission var1) {
        return this.permissionRepository.save(var1);
    }

    @Override
    public Optional<Permission> findById(UUID var1) {
        return this.permissionRepository.findById(var1);
    }

    @Override
    public List<Permission> findAll() {
        return this.permissionRepository.findAll();
    }

    @Override
    public void deleteById(UUID var1) {
        this.deleteById(var1);
    }

}
