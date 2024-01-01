package com.eguglielmelli.repositories;

import com.eguglielmelli.models.Payee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayeeRepository extends JpaRepository<Payee,Long> {
}
