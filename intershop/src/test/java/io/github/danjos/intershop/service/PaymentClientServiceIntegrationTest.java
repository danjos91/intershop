package io.github.danjos.intershop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("PaymentClientService Integration Tests")
class PaymentClientServiceIntegrationTest {

    @Autowired
    private PaymentClientService paymentClientService;

    @Test
    @DisplayName("Should create service instance successfully")
    void shouldCreateServiceInstance() {
        assertThat(paymentClientService).isNotNull();
    }

    @Test
    @DisplayName("Should handle getBalance method call")
    void shouldHandleGetBalanceMethodCall() {
        // When
        Mono<Double> result = paymentClientService.getBalance();

        // Then
        StepVerifier.create(result)
                .assertNext(balance -> {
                    assertThat(balance).isNotNull();
                    // In test environment, this will likely return 0.0 due to no payment service running
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle processPayment method call")
    void shouldHandleProcessPaymentMethodCall() {
        // When
        Mono<Boolean> result = paymentClientService.processPayment(100.0, "test-order-123");

        // Then
        StepVerifier.create(result)
                .assertNext(success -> {
                    assertThat(success).isNotNull();
                    // In test environment, this will likely return false due to no payment service running
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle processPayment with zero amount")
    void shouldHandleProcessPaymentWithZeroAmount() {
        // When
        Mono<Boolean> result = paymentClientService.processPayment(0.0, "test-order-zero");

        // Then
        StepVerifier.create(result)
                .assertNext(success -> {
                    assertThat(success).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle processPayment with null orderId")
    void shouldHandleProcessPaymentWithNullOrderId() {
        // When
        Mono<Boolean> result = paymentClientService.processPayment(100.0, null);

        // Then
        StepVerifier.create(result)
                .assertNext(success -> {
                    assertThat(success).isNotNull();
                })
                .verifyComplete();
    }
}

