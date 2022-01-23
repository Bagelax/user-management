package usermanagementbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usermanagementbe.model.Machine;
import usermanagementbe.model.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID> {
    public List<Machine> findByCreatedBy(User user);
}
