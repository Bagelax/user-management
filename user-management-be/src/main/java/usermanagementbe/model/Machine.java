package usermanagementbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "gen_random_uuid")
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MachineStatus status;

    @JsonIgnore
    @ManyToOne
    private User createdBy;

    private Timestamp createdAt;

    private boolean active;
}
