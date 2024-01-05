package com.eguglielmelli.repositories;
import com.eguglielmelli.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findByAccountName(String accountName);

    Optional<Account> findByAccountId(Long accountId);

    List<Account> findByUserUserId(Long userId);
}
