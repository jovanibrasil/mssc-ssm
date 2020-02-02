package com.mssc.ssm.services;

import com.mssc.ssm.domain.Payment;
import com.mssc.ssm.domain.PaymentEvent;
import com.mssc.ssm.domain.PaymentState;
import com.mssc.ssm.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp(){
        paymentRepository.deleteAll();
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void testPreAuth(){
        Random randomNumberMock = Mockito.mock(Random.class);
        when(randomNumberMock.nextInt(anyInt())).thenReturn(1);

        Payment savedPayment = paymentService.newPayment(payment);
        assertEquals(PaymentState.NEW, savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
        Payment preAuthPayment = paymentRepository.getOne(savedPayment.getId());
        assertEquals(PaymentState.PRE_AUTH, sm.getState().getId());
    }

    @Transactional
    @Test
    void testAuth(){
        Random randomNumberMock = Mockito.mock(Random.class);
        when(randomNumberMock.nextInt(anyInt())).thenReturn(1);

        Payment savedPayment = paymentService.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> sm;
        paymentService.preAuth(savedPayment.getId());

        sm = paymentService.authorizePayment(savedPayment.getId());

        assertEquals(PaymentState.AUTH, sm.getState().getId());
    }

}