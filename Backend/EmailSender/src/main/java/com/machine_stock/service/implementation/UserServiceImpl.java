package com.machine_stock.service.implementation;

import com.machine_stock.model.Confirmation;
import com.machine_stock.model.User;
import com.machine_stock.repository.ConfirmationRepository;
import com.machine_stock.repository.UserRepository;
import com.machine_stock.service.EmailService;
import com.machine_stock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;
    @Override
    public User saveUser(User user)
    {
        if (userRepository.existsByEmail(user.getEmail())) throw new RuntimeException("Email already exists");

        user.setEnabled(false);
        userRepository.save(user);

        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);

        emailService.sendHtmlPage(user.getEmail(), confirmation.getToken());
        return user;
    }

    @Override
    public Boolean verifyToken(String token)
    {
        Confirmation confirmation = confirmationRepository.findConfirmationByToken(token);
        User user = userRepository.findUserByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);

        userRepository.save(user);
        confirmationRepository.delete(confirmation);
        return true;
    }
}
