package usermanagementbe.service;

import org.springframework.stereotype.Service;
import usermanagementbe.model.ErrorMessage;
import usermanagementbe.model.User;
import usermanagementbe.repository.ErrorMessageRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ErrorMessageService implements IService<ErrorMessage, UUID> {

    private final ErrorMessageRepository errorMessageRepository;

    public ErrorMessageService(ErrorMessageRepository errorMessageRepository) {
        this.errorMessageRepository = errorMessageRepository;
    }

    @Override
    public ErrorMessage save(ErrorMessage var1) {
        return this.errorMessageRepository.save(var1);
    }

    @Override
    public Optional<ErrorMessage> findById(UUID var1) {
        return this.errorMessageRepository.findById(var1);
    }

    @Override
    public List<ErrorMessage> findAll() {
        return this.errorMessageRepository.findAll();
    }

    @Override
    public void deleteById(UUID var1) {
        this.errorMessageRepository.deleteById(var1);
    }

    public List<ErrorMessage> findByUser(User user) {
        return this.errorMessageRepository.findByUserId(user.getId());
    }
}
