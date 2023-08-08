package com.machine_stock.repository;

import com.machine_stock.model.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, Long>
{
    Confirmation findConfirmationByToken(String token);
}
