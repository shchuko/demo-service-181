package com.itmo.microservices.shop.delivery.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class DeliveryInfoRecordDto {

    @Id
    @GeneratedValue
    private UUID id;
    private String outcome;
    private long preparedTime;
    private int attempts;
    private long submittedTime;
    private UUID transactionId;
    private long submissionStartedTime;

    private UUID orderId;

    @JsonIgnore
    public UUID getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }


    public DeliveryInfoRecordDto() {

    }

    @JsonIgnore
    public UUID getId() {
        return id;
    }

    @JsonIgnore
    public void setId(UUID id) {
        this.id = id;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public long getPreparedTime() {
        return preparedTime;
    }

    public void setPreparedTime(long preparedTime) {
        this.preparedTime = preparedTime;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public long getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(long submittedTime) {
        this.submittedTime = submittedTime;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public long getSubmissionStartedTime() {
        return submissionStartedTime;
    }

    public void setSubmissionStartedTime(long submissionStartedTime) {
        this.submissionStartedTime = submissionStartedTime;
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
