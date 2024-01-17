package com.eguglielmelli.service;
import com.eguglielmelli.models.Payee;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.PayeeRepository;
import com.eguglielmelli.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayeeServiceTest {

    @Mock
    private PayeeRepository payeeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PayeeService payeeService;

    private User testUser;
    private Payee testPayee;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");

        testPayee = new Payee();
        testPayee.setPayeeId(1L);
        testPayee.setName("Test Payee");
        testPayee.setUser(testUser);
    }
    @Test
    void whenCreatePayee_thenPayeeCreated() {
        when(userRepository.findByUserId(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(payeeRepository.save(any(Payee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payee createdPayee = payeeService.createPayee("Test Payee", testUser.getUserId());

        assertNotNull(createdPayee);
        assertEquals("Test Payee", createdPayee.getName());
        assertEquals(testUser, createdPayee.getUser());
        verify(payeeRepository).save(any(Payee.class));
    }
    @Test
    void whenChangePayeeName_thenPayeeNameUpdated() {
        Long payeeId = testPayee.getPayeeId();
        String newName = "Updated Payee Name";
        when(payeeRepository.findById(payeeId)).thenReturn(Optional.of(testPayee));
        when(payeeRepository.save(any(Payee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payee updatedPayee = payeeService.changePayeeName(payeeId, newName);

        assertNotNull(updatedPayee);
        assertEquals(newName, updatedPayee.getName());
        verify(payeeRepository).save(any(Payee.class));
    }
    @Test
    void whenDeletePayee_thenPayeeDeleted() {
        when(payeeRepository.findById(testPayee.getPayeeId())).thenReturn(Optional.of(testPayee));

        payeeService.deletePayee(testPayee.getPayeeId());

        verify(payeeRepository).delete(testPayee);
    }

    @Test
    void whenGetAllPayees_thenAllPayeesReturned() {
        List<Payee> payeeList = List.of(testPayee);
        when(payeeRepository.findAll()).thenReturn(payeeList);

        List<Payee> payees = payeeService.getAllPayees();

        assertFalse(payees.isEmpty());
        assertEquals(1, payees.size());
        assertEquals(testPayee, payees.get(0));
        verify(payeeRepository).findAll();
    }
}
