package com.itmo.microservices.shop.payment.repository;

import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.repository.PaymentStatusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;

@ActiveProfiles("dev")
public class PaymentStatusRepositoryTest extends DataJpaTestCase {
    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Autowired
    private PaymentStatusRepository repository;

    @BeforeEach
    public void fillTable() {
        repository.saveAll(hardcodedValues.paymentStatuses);
    }

    @Test
    public void findPaymentStatusByIdIsEquals() {
        int index = (new Random()).nextInt(hardcodedValues.paymentStatuses.size());
        int status_id = hardcodedValues.paymentStatuses.get(index).getId();
        var expected = repository.findPaymentStatusById(status_id).getName();

        var actual = hardcodedValues.paymentStatuses.stream()
                .filter(i -> i.getId() == status_id).findFirst().get().getName();

        Assertions.assertEquals(actual, expected);
    }

}
