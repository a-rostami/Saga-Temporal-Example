package com.arashrostami.temporalsaga.api;

import com.arashrostami.temporalsaga.domain.OrderStatus;
import java.time.Instant;

// Order details response
public class OrderResponse {
  private final String orderId;
  private final OrderStatus status;
  private final long totalCents;
  private final String currency;
  private final String inventoryReservationId;
  private final String paymentId;
  private final String shipmentId;
  private final String workflowRunId;
  private final Instant createdAt;
  private final Instant updatedAt;

  public OrderResponse(
      String orderId,
      OrderStatus status,
      long totalCents,
      String currency,
      String inventoryReservationId,
      String paymentId,
      String shipmentId,
      String workflowRunId,
      Instant createdAt,
      Instant updatedAt
  ) {
    this.orderId = orderId;
    this.status = status;
    this.totalCents = totalCents;
    this.currency = currency;
    this.inventoryReservationId = inventoryReservationId;
    this.paymentId = paymentId;
    this.shipmentId = shipmentId;
    this.workflowRunId = workflowRunId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getOrderId() {
    return orderId;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public long getTotalCents() {
    return totalCents;
  }

  public String getCurrency() {
    return currency;
  }

  public String getInventoryReservationId() {
    return inventoryReservationId;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public String getShipmentId() {
    return shipmentId;
  }

  public String getWorkflowRunId() {
    return workflowRunId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
