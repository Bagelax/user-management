package usermanagementbe.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
public class ErrorMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "gen_random_uuid")
    private UUID id;

    private Timestamp time;

    private String error;

    @ManyToOne
    private Machine machine;

    @OneToOne
    private ScheduledOperation scheduledOperation;

}
