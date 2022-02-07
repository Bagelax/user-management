package usermanagementbe.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import usermanagementbe.model.*;
import usermanagementbe.service.ConfigService;
import usermanagementbe.service.MachineService;
import usermanagementbe.service.ScheduledOperationService;
import usermanagementbe.utils.ErrorResponseBody;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/machines")
public class MachineController extends PermissionCheck {
    private final MachineService machineService;
    private final ConfigService configService;
    private final ScheduledOperationService scheduledOperationService;

    public MachineController(MachineService machineService,
                             ConfigService configService,
                             ScheduledOperationService scheduledOperationService) {
        this.machineService = machineService;
        this.configService = configService;
        this.scheduledOperationService = scheduledOperationService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAll() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(200).body(this.machineService.findByUser(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") UUID id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (Machine m : this.machineService.findByUser(currentUser)) {
            if (m.getId().equals(id))
                return ResponseEntity.status(200).body(m);
        }
        return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMachine(@RequestBody NewMachineRequestBody body) {
        Permissions requiredPermission = Permissions.CAN_CREATE_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));
        Machine machine = new Machine();
        machine.setName(body.getName());
        machine.setCreatedBy((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        Timestamp t = new Timestamp(now.toEpochSecond() * 1000);
        machine.setCreatedAt(t);
        machine.setActive(true);
        machine.setStatus(MachineStatus.STOPPED);
        this.machineService.save(machine);
        return ResponseEntity.status(200).body(machine);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> searchMachines(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) ArrayList<String> status,
                                            @RequestParam(required = false) Long dateFrom,
                                            @RequestParam(required = false) Long dateTo) {
        HashMap<String, Object> filterConfig = new HashMap<>();
        filterConfig.put("name", name);
        List<MachineStatus> statuses = new ArrayList<>();
        if (status != null)
            for (String s : status)
                statuses.add(MachineStatus.valueOf(s));
        filterConfig.put("statuses", statuses);
        if (dateFrom != null)
            filterConfig.put("dateFrom", new Timestamp(dateFrom));
        else
            filterConfig.put("dateFrom", null);
        if (dateTo != null)
            filterConfig.put("dateTo", new Timestamp(dateTo));
        else
            filterConfig.put("dateTo", null);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(200).body(this.machineService.filter(currentUser, filterConfig));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMachine(@PathVariable UUID id) {
        Permissions requiredPermission = Permissions.CAN_CREATE_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.STOPPED)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not STOPPED"));
        targetMachine.get().setActive(false);
        this.machineService.save(targetMachine.get());
        return ResponseEntity.status(200).body(new UserDeletedResponseBody(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startMachine(@PathVariable UUID id) {
        Permissions requiredPermission = Permissions.CAN_START_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.STOPPED)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not STOPPED"));
        ScheduledOperation so = new ScheduledOperation();
        so.setMachine(targetMachine.get());
        so.setOperation(Operation.START);

        this.configService.getOperationQ().add(so);
        return ResponseEntity.status(202).body("");
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopMachine(@PathVariable UUID id) {
        Permissions requiredPermission = Permissions.CAN_STOP_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.RUNNING)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not RUNNING"));
        ScheduledOperation so = new ScheduledOperation();
        so.setMachine(targetMachine.get());
        so.setOperation(Operation.STOP);

        this.configService.getOperationQ().add(so);
        return ResponseEntity.status(202).body("");
    }

    @PostMapping("/{id}/restart")
    public ResponseEntity<?> restartMachine(@PathVariable UUID id) {
        Permissions requiredPermission = Permissions.CAN_RESTART_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.RUNNING)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not STOPPED"));
        ScheduledOperation so = new ScheduledOperation();
        so.setMachine(targetMachine.get());
        so.setOperation(Operation.RESTART);

        this.configService.getOperationQ().add(so);
        return ResponseEntity.status(202).body("");
    }

    @PostMapping(value = "/{id}/schedule/start", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleStartMachine(@PathVariable UUID id, @RequestBody ScheduleOperationRequestBody sor) {
        Permissions requiredPermission = Permissions.CAN_START_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.STOPPED)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not STOPPED"));
        ScheduledOperation so = new ScheduledOperation();
        so.setMachine(targetMachine.get());
        so.setOperation(Operation.START);
        so.setExecuteAt(new Timestamp(sor.getExecuteAt()));
        this.scheduledOperationService.save(so);
        return ResponseEntity.status(202).body("");
    }

    @PostMapping(value = "/{id}/schedule/stop", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleStopMachine(@PathVariable UUID id, @RequestBody ScheduleOperationRequestBody sor) {
        Permissions requiredPermission = Permissions.CAN_STOP_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.RUNNING)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not RUNNING"));
        ScheduledOperation so = new ScheduledOperation();
        so.setMachine(targetMachine.get());
        so.setOperation(Operation.STOP);
        so.setExecuteAt(new Timestamp(sor.getExecuteAt()));
        this.scheduledOperationService.save(so);
        return ResponseEntity.status(202).body("");
    }

    @PostMapping(value = "/{id}/schedule/restart", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleRestartMachine(@PathVariable UUID id, @RequestBody ScheduleOperationRequestBody sor) {
        Permissions requiredPermission = Permissions.CAN_START_MACHINES;
        if (!this.checkPermission(requiredPermission))
            return ResponseEntity.status(403).body(new MissingPermissionsErrorResponseBody(requiredPermission.name()));

        Optional<Machine> targetMachine = this.machineService.findById(id);
        if (!targetMachine.isPresent())
            return ResponseEntity.status(404).body(new ErrorResponseBody(String.format("Machine with id %s not found", id)));
        if (targetMachine.get().getStatus() != MachineStatus.STOPPED)
            return ResponseEntity.status(422).body(new ErrorResponseBody("Machine status is not STOPPED"));
        ScheduledOperation so = new ScheduledOperation();
        so.setMachine(targetMachine.get());
        so.setOperation(Operation.RESTART);
        so.setExecuteAt(new Timestamp(sor.getExecuteAt()));
        this.scheduledOperationService.save(so);
        return ResponseEntity.status(202).body("");
    }
}
