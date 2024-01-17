package com.eguglielmelli.service;
import com.eguglielmelli.models.Account;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.AccountRepository;
import com.eguglielmelli.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");

        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setAccountName("Test Account");
        testAccount.setAccountType("savings");
        testAccount.setUser(testUser);
        testAccount.setBalance(BigDecimal.ZERO);
    }
    @Test
    void whenCreateAccount_thenAccountCreated() {
        BigDecimal amount = BigDecimal.valueOf(100);
        testAccount.setBalance(amount);

        when(userRepository.findByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(accountRepository.findByAccountName(testAccount.getAccountName())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account createdAccount = accountService.createAccount(testAccount, testUser.getUserId());

        assertNotNull(createdAccount);
        assertEquals(amount, createdAccount.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void whenUpdateAccountName_thenAccountNameUpdated() {

        Long accountId = testAccount.getAccountId();
        String newName = "Updated Account Name";
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setAccountName(newName);
            return acc;
        });


        Account updatedAccount = accountService.updateAccountName(accountId, newName);


        assertNotNull(updatedAccount);
        assertEquals(newName, updatedAccount.getAccountName());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void whenDeleteAccount_thenAccountDeleted() {

        Long accountId = testAccount.getAccountId();
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(testAccount));

        accountService.deleteAccount(accountId);

        verify(accountRepository).delete(testAccount);
    }

}
