package com.itmo.microservices.shop.delivery.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import liquibase.pro.packaged.O;

import java.util.UUID;

public class DeliveryInfoRecordDto {
    private final String outcome;
    private final long preparedTime;
    private final int attempts;
    private final long submittedTime;
    private final UUID transactionId;
    private final long submissionStartedTime;


    /* TODO remove */
    @JsonIgnore
    private final UUID orderId;

    @JsonIgnore
    public UUID getOrderId() {
        return orderId;
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

    public String getOutcome() {
        return outcome;
    }

    public long getPreparedTime() {
        return preparedTime;
    }

    public int getAttempts() {
        return attempts;
    }

    public long getSubmittedTime() {
        return submittedTime;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public long getSubmissionStartedTime() {
        return submissionStartedTime;
    }

}
