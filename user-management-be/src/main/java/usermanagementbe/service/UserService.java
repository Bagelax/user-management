package usermanagementbe.service;

import org.springframework.stereotype.Service;
import usermanagementbe.model.User;
import usermanagementbe.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IService<User, UUID> {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User var1) {
        return this.userRepository.save(var1);
    }

    @Override
    public Optional<User> findById(UUID var1) {
        return this.userRepository.findById(var1);
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public void deleteById(UUID var1) {
        this.userRepository.deleteById(var1);
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
