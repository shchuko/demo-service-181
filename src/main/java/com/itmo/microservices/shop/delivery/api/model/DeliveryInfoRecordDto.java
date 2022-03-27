package com.itmo.microservices.shop.delivery.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class DeliveryInfoRecordDto {

    @Id
    private UUID id;
    private String outcome;
    private long preparedTime;
    private int attempts;
    private long submittedTime;
    private UUID transactionId;
    private long submissionStartedTime;


    /* TODO remove */
    @JsonIgnore
    private UUID orderId;

    public DeliveryInfoRecordDto() {

    }

    public enum Outcome {
        SUCCESS, FAILURE, EXPIRED
    }

    public DeliveryInfoRecordDto(Outcome outcome, long preparedTime, int attempts, long submittedTime, UUID transactionId, long submissionStartedTime, UUID orderId) {
        this.outcome = outcome.name();
        this.preparedTime = preparedTime;
        this.attempts = attempts;
        this.submittedTime = submittedTime;
        this.transactionId = transactionId;
        this.submissionStartedTime = submissionStartedTime;
        this.orderId = orderId;
    }

    public DeliveryInfoRecordDto(String outcome, long preparedTime, int attempts, long submittedTime, UUID transactionId, long submissionStartedTime, UUID orderId) {
        this(Outcome.valueOf(outcome), preparedTime, attempts, submittedTime, transactionId, submissionStartedTime, orderId);
    }

}
