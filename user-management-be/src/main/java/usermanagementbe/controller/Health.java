package usermanagementbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usermanagementbe.model.Machine;
import usermanagementbe.model.MachineStatus;
import usermanagementbe.model.Operation;
import usermanagementbe.model.ScheduledOperation;
import usermanagementbe.service.ConfigService;
import usermanagementbe.service.MachineService;
import usermanagementbe.service.ScheduledOperationService;
import usermanagementbe.service.UserService;

@RestController
public class Health {
    private final ConfigService configService;
    private final ScheduledOperationService scheduledOperationService;
    private final MachineService machineService;
    private final UserService userService;

    public Health(ConfigService configService,
                  ScheduledOperationService scheduledOperationService,
                  MachineService machineService,
                  UserService userService) {
        this.configService = configService;
        this.scheduledOperationService = scheduledOperationService;
        this.machineService = machineService;
        this.userService = userService;
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        ScheduledOperation so = new ScheduledOperation();
        so.setOperation(Operation.START);
        Machine machine = new Machine();
        machine.setStatus(MachineStatus.STOPPED);
        machine.setActive(true);
        machine.setName("Makina");
        machine.setCreatedBy(this.userService.findAll().get(0));
        this.machineService.save(machine);
        so.setMachine(machine);

        System.out.println("HEalth oq: " + this.configService.getOperationQ().hashCode());
        try {
            this.configService.getOperationQ().put(so);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Ok");
    }

}
