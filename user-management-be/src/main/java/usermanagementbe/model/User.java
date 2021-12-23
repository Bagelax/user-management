package usermanagementbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "gen_random_uuid")
    private UUID id;
    private String name;
    private String surname;
    private String email;
    @JsonIgnore
    private String password;

    @Transient
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permissions;

    public static final String hashPassword(String password) {
        return User.encoder.encode(password);
    }

    public boolean checkPassword(String password) {
        return this.encoder.matches(password, this.password);
    }

    public void setPassword(String password) {
        this.password = User.hashPassword(password);
    }
}
