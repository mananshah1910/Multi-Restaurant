package com.cravedash.order.client;

import com.cravedash.order.dto.PaymentDto;
import com.cravedash.order.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentDto createPayment(@RequestBody PaymentRequest paymentRequest);
}
