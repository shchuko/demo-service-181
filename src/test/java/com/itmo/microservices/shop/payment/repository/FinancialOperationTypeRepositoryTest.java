package com.itmo.microservices.shop.payment.repository;

import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import com.itmo.microservices.shop.payment.common.HardcodedValues;
import com.itmo.microservices.shop.payment.impl.entity.FinancialOperationType;
import com.itmo.microservices.shop.payment.impl.repository.FinancialOperationTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class FinancialOperationTypeRepositoryTest extends DataJpaTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Autowired
    private FinancialOperationTypeRepository repository;

    @Test
    public void findFinancialOperationTypeByIdIsEquals() {
        this.fillTable();
        FinancialOperationType financialOperationTypeTest = repository.findFinancialOperationTypeById(1);
        Assertions.assertEquals(financialOperationTypeTest, hardcodedValues.financialOperationTypes.get(0));
    }

    private void fillTable() {
        repository.saveAll(hardcodedValues.financialOperationTypes);
    }
}
