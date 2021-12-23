package usermanagementbe.model;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "gen_random_uuid")
    private UUID id;
    private String name;
}
