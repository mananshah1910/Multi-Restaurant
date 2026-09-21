package com.cravedash.payment;

import com.cravedash.payment.entity.Payment;
import com.cravedash.payment.repository.PaymentRepository;
import com.cravedash.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment(1L, 1001L, 500.0, "PENDING");
    }

    @Test
    void createPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment created = paymentService.createPayment(payment);

        assertNotNull(created);
        assertEquals(500.0, created.getAmount());
        assertEquals("PENDING", created.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment updated = paymentService.updateStatus(1L, "PAID");

        assertNotNull(updated);
        assertEquals("PAID", updated.getPaymentStatus());
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> paymentService.getPaymentById(999L));
    }
}
