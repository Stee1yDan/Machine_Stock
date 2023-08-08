package com.machine_stock.emailsender.repository;

import com.machine_stock.emailsender.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>
{

}
