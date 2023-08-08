package com.machine_stock.service;

import com.machine_stock.model.User;

public interface UserService
{
    User saveUser(User user);
    Boolean verifyToken(String token);
}
