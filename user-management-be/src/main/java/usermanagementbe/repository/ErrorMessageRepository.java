package usermanagementbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usermanagementbe.model.ErrorMessage;

import java.util.List;
import java.util.UUID;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, UUID> {
    @Query(value = "select e.* from error_message e join machine m on e.machine_id = m.id where m.created_by_id = ?1",
            nativeQuery = true
    )
    List<ErrorMessage> findByUserId(UUID userId);
}
