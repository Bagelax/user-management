package usermanagementbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usermanagementbe.model.ScheduledOperation;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface ScheduledOperationRepository extends JpaRepository<ScheduledOperation, UUID> {
    @Query(value = "select * from scheduled_operation r where r.execute_at >= ?1 and r.execute_at <= ?2 and r.executed is false",
            nativeQuery = true
    )
    List<ScheduledOperation> findInTimeRange(Timestamp t1, Timestamp t2);
}
