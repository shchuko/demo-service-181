package com.itmo.microservices.shop.payment.impl.repository;

import com.itmo.microservices.shop.payment.impl.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    PaymentStatus findPaymentStatusById(Integer id);
    PaymentStatus findByName(String name);


    enum VALUES {
        FAILED, SUCCESS
    }
}
