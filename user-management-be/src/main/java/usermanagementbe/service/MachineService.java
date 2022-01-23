package usermanagementbe.service;

import org.springframework.stereotype.Service;
import usermanagementbe.model.Machine;
import usermanagementbe.model.MachineStatus;
import usermanagementbe.model.Operation;
import usermanagementbe.model.User;
import usermanagementbe.repository.MachineRepository;

import java.sql.Timestamp;
import java.util.*;

@Service
public class MachineService implements IService<Machine, UUID> {

    private final MachineRepository machineRepository;

    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public Machine save(Machine var1) {
        return this.machineRepository.save(var1);
    }

    @Override
    public Optional<Machine> findById(UUID var1) {
        Optional<Machine> m = this.machineRepository.findById(var1);
        if (m.isPresent() && !m.get().isActive())
            return Optional.empty();
        return m;
    }

    @Override
    public List<Machine> findAll() {
        List<Machine> machines = this.machineRepository.findAll();
        List<Machine> response = new ArrayList<>();
        for (Machine m : machines)
            if (m.isActive()) response.add(m);
        return response;
    }

    @Override
    public void deleteById(UUID var1) {
        this.machineRepository.deleteById(var1);
    }

    public List<Machine> findByUser(User user) {
        List<Machine> machines = this.machineRepository.findByCreatedBy(user);
        List<Machine> response = new ArrayList<>();
        for (Machine m : machines)
            if (m.isActive()) response.add(m);
        return response;
    }

    public List<Machine> filter(User user, HashMap<String, Object> filterArgs) {
        List<Machine> machines = new ArrayList<Machine>();
        for (Machine m : this.machineRepository.findByCreatedBy(user)) {
            if (!m.isActive())
                continue;
            String name = (String) filterArgs.get("name");
            if (name != null && !(m.getName().toLowerCase().contains(name.toLowerCase())))
                continue;
            List<MachineStatus> statuses = (ArrayList<MachineStatus>) filterArgs.get("statuses");
            if (!statuses.isEmpty() && !statuses.contains(m.getStatus()))
                continue;
            Timestamp dateFrom = (Timestamp) filterArgs.get("dateFrom");
            if (dateFrom != null && m.getCreatedAt().before(dateFrom))
                continue;
            Timestamp dateTo = (Timestamp) filterArgs.get("dateTo");
            if (dateTo != null && m.getCreatedAt().after(dateTo))
                continue;
            machines.add(m);
        }
        return machines;
    }
}
