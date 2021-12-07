package com.itmo.microservices.shop.payment.impl.repository;

import com.itmo.microservices.shop.payment.impl.entity.FinancialOperationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialOperationTypeRepository extends JpaRepository<FinancialOperationType, Integer> {

    FinancialOperationType findFinancialOperationTypeById(Integer id);

    FinancialOperationType findFinancialOperationTypeByName(String name);

    enum VALUES {
        WITHDRAW, REFUND
    }
}
