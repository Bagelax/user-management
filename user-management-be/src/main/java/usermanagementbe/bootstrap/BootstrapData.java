package usermanagementbe.bootstrap;

import usermanagementbe.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import usermanagementbe.repository.MachineRepository;
import usermanagementbe.repository.PermissionRepository;
import usermanagementbe.repository.ScheduledOperationRepository;
import usermanagementbe.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final MachineRepository machineRepository;
    private final ScheduledOperationRepository scheduledOperationRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository,
                         PermissionRepository permissionRepository,
                         MachineRepository machineRepository,
                         ScheduledOperationRepository scheduledOperationRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.machineRepository = machineRepository;
        this.scheduledOperationRepository = scheduledOperationRepository;
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

        Machine machine = new Machine();
        machine.setName("M1");
        machine.setActive(true);
        machine.setStatus(MachineStatus.STOPPED);
        machine.setCreatedBy(admin);
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        machine.setCreatedAt(new Timestamp(now.toEpochSecond() * 1000));
        this.machineRepository.save(machine);

        ScheduledOperation s = new ScheduledOperation();
        s.setMachine(machine);
        s.setExecuteAt(new Timestamp(now.toEpochSecond() * 1000));
        s.setOperation(Operation.START);
        this.scheduledOperationRepository.save(s);

        ScheduledOperation s2 = new ScheduledOperation();
        s2.setMachine(machine);
        s2.setExecuteAt(new Timestamp(now.minusMinutes(2).toEpochSecond() * 1000));
        s2.setOperation(Operation.START);
        this.scheduledOperationRepository.save(s2);

        ScheduledOperation s3 = new ScheduledOperation();
        s3.setMachine(machine);
        s3.setExecuteAt(new Timestamp(now.plusMinutes(2).toEpochSecond() * 1000));
        s3.setOperation(Operation.START);
        this.scheduledOperationRepository.save(s3);

        Timestamp t1 = new Timestamp(now.toEpochSecond() * 1000);
        Timestamp t2 = new Timestamp(now.minusMinutes(1).toEpochSecond() * 1000);
        System.out.println(this.scheduledOperationRepository.findInTimeRange(t2, t1).size());
    }
}
