package com.machine_stock.emailsender.service.impl;

import com.machine_stock.emailsender.model.User;
import com.machine_stock.emailsender.repository.UserRepository;
import com.machine_stock.emailsender.service.EmailService;
import com.machine_stock.emailsender.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final EmailService emailService;
    @Override
    public User saveUser(User user)
    {
        emailService.sendBasicEmailMessage(user.getEmail());
        return userRepository.save(user);
    }
}
