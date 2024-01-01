package com.eguglielmelli.service;
import com.eguglielmelli.exceptions.UserNotFoundException;
import com.eguglielmelli.models.Payee;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.PayeeRepository;
import com.eguglielmelli.repositories.UserRepository;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class PayeeService {
    private final PayeeRepository payeeRepository;
    private final UserRepository userRepository;

    @Autowired
    public PayeeService(PayeeRepository payeeRepository,UserRepository userRepository) {
        this.payeeRepository = payeeRepository;
        this.userRepository = userRepository;
    }

    public Payee createPayee(String name, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Payee payee = new Payee();
        name = WordUtils.capitalizeFully(name);
        payee.setName(name);
        payee.setUser(user);

        return payeeRepository.save(payee);
    }


    @Transactional
    public Payee changePayeeName(Long id, String newName) {
        Payee payee = payeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payee with ID " + id + " is not found."));
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Payee name cannot be null or empty.");
        }
        payee.setName(WordUtils.capitalizeFully(newName));
        return payeeRepository.save(payee);
    }

    @Transactional
    public void deletePayee(Long id) {
        Payee payee = payeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Payee with that ID not found."));
        payeeRepository.delete(payee);
    }

    public List<Payee> getAllPayees() {
        return payeeRepository.findAll();
    }

}
