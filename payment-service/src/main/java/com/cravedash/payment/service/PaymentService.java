package com.cravedash.payment.service;

import com.cravedash.payment.entity.Payment;
import com.cravedash.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        if (payment.getOrderId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrderId is required");
        }
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid amount is required");
        }
        if (payment.getPaymentStatus() == null || payment.getPaymentStatus().isBlank()) {
            payment.setPaymentStatus("PENDING");
        }
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with id: " + id));
    }

    public Payment updatePayment(Long id, Payment updated) {
        Payment existing = getPaymentById(id);
        if (updated.getOrderId() != null) existing.setOrderId(updated.getOrderId());
        if (updated.getAmount() != null) existing.setAmount(updated.getAmount());
        if (updated.getPaymentStatus() != null) existing.setPaymentStatus(updated.getPaymentStatus());
        return paymentRepository.save(existing);
    }

    public Payment updateStatus(Long id, String status) {
        Payment existing = getPaymentById(id);
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status cannot be empty");
        }
        existing.setPaymentStatus(status.toUpperCase());
        return paymentRepository.save(existing);
    }

    public void deletePayment(Long id) {
        Payment existing = getPaymentById(id);
        paymentRepository.delete(existing);
    }
}
