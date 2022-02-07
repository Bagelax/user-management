package usermanagementbe.service;

import org.springframework.stereotype.Service;
import usermanagementbe.model.ScheduledOperation;
import usermanagementbe.repository.ScheduledOperationRepository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduledOperationService implements IService<ScheduledOperation, UUID> {

    private final ScheduledOperationRepository scheduledOperationRepository;

    public ScheduledOperationService(ScheduledOperationRepository scheduledOperationRepository) {
        this.scheduledOperationRepository = scheduledOperationRepository;
    }


    @Override
    public ScheduledOperation save(ScheduledOperation var1) {
        return this.scheduledOperationRepository.save(var1);
    }

    @Override
    public Optional<ScheduledOperation> findById(UUID var1) {
        return this.scheduledOperationRepository.findById(var1);
    }

    @Override
    public List<ScheduledOperation> findAll() {
        return this.scheduledOperationRepository.findAll();
    }

    @Override
    public void deleteById(UUID var1) {
        this.scheduledOperationRepository.deleteById(var1);
    }

    public List<ScheduledOperation> findInLastSecond() {
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        Timestamp t1 = new Timestamp(now.toEpochSecond() * 1000);
        Timestamp t2 = new Timestamp(now.minusSeconds(1).toEpochSecond() * 1000);
        return this.scheduledOperationRepository.findInTimeRange(t2, t1);
    }
}
