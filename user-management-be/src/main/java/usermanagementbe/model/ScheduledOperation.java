package usermanagementbe.model;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
public class ScheduledOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "gen_random_uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    private Timestamp executeAt;

    @ManyToOne
    private Machine machine;

    @ColumnDefault("false")
    private boolean executed;
}
