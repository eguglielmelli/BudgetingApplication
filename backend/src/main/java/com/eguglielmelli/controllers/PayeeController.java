package com.eguglielmelli.controllers;
import com.eguglielmelli.models.Payee;
import com.eguglielmelli.service.PayeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payee")
public class PayeeController {

    private final PayeeService payeeService;

    public PayeeController(PayeeService payeeService) {
        this.payeeService = payeeService;
    }

    @GetMapping("/")
    public List<Payee> getAllPayees() {
        return payeeService.getAllPayees();
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Payee> createPayee(@RequestBody Payee payee,@PathVariable Long userId) {
        validatePayee(payee);
        Payee newPayee = payeeService.createPayee(payee.getName(),userId);
        return new ResponseEntity<>(newPayee,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payee> updatePayeeName(@PathVariable Long id, @RequestBody Payee updatedPayee) {
        validatePayee(updatedPayee);
        Payee updated = payeeService.changePayeeName(id, updatedPayee.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayee(@PathVariable Long id) {
        payeeService.deletePayee(id);
        return ResponseEntity.ok().build();
    }


    private void validatePayee(Payee payee) {
        if(payee == null || payee.getName() == null || payee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Payee name cannot be null or empty.");
        }
    }
}
