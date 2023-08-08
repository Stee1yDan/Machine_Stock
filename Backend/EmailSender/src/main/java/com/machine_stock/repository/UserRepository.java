package com.machine_stock.repository;

import com.machine_stock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long>
{
    User findUserByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
}
