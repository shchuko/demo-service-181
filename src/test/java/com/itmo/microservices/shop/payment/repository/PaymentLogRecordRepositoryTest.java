package com.itmo.microservices.shop.payment.repository;

import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.repository.PaymentLogRecordRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@ActiveProfiles("dev")
public class PaymentLogRecordRepositoryTest extends DataJpaTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Autowired
    private PaymentLogRecordRepository repository;

    @BeforeEach
    public void fillTable() {
        repository.saveAll(hardcodedValues.paymentLogRecords);
    }

    @Test
    public void findByUserIdIsEquals() {
        int index = (new Random()).nextInt(hardcodedValues.userIds.size());
        UUID userId = hardcodedValues.userIds.get(index);
        var records = repository.findByUserId(userId);
        records.forEach(i -> i.setId(null));

        var correctRecords = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId).collect(Collectors.toList());
        correctRecords.forEach(i -> i.setId(null));

        Assertions.assertEquals(correctRecords, records);
    }

    @Test
    public void findByUserIdAndOrderIdIsEquals() {
        int index = (new Random()).nextInt(hardcodedValues.userIds.size());
        UUID userId = hardcodedValues.userIds.get(index);
        index = (new Random()).nextInt(hardcodedValues.orderIds.size());
        UUID orderId = hardcodedValues.userIds.get(index);

        var records = repository.findByUserIdAndOrderId(userId, orderId);
        records.forEach(i -> i.setId(null));

        var correctRecords = hardcodedValues.paymentLogRecords.stream()
                .filter(i -> i.getUserId() == userId && i.getOrderId() == orderId).collect(Collectors.toList());
        correctRecords.forEach(i -> i.setId(null));

        Assertions.assertEquals(correctRecords, records);
    }

}
