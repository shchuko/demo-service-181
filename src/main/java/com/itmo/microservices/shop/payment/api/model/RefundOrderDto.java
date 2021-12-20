package com.itmo.microservices.shop.payment.api.model;

import com.itmo.microservices.shop.payment.impl.entity.RefundOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class RefundOrderDto  implements Comparable<RefundOrderDto>{

    @NotNull
    private UUID orderId;
    @NotNull
    private Double price;
    @NotNull
    private Date requestTime;
    private UUID transactionId;

    public static RefundOrderDto toModel(RefundOrder refundOrder) {
        RefundOrderDto refundOrderDto = new RefundOrderDto();
        refundOrderDto.setRequestTime(refundOrder.getRequestTime());
        refundOrderDto.setOrderId(refundOrder.getOrderId());
        refundOrderDto.setPrice(refundOrder.getPrice());
        refundOrderDto.setTransactionId(refundOrder.getTransactionId());
        return refundOrderDto;
    }

    @Override
    public int compareTo(@NotNull RefundOrderDto o) {
        return Long.compare(this.getRequestTime().getTime(), o.getRequestTime().getTime());
    }

}
