package com.arashrostami.temporalsaga.domain;

import java.util.List;

// Workflow input for order
public class OrderInput {
  private String orderId;
  private long totalCents;
  private String currency;
  private List<OrderItem> items;
  private FailStep failStep;

  public OrderInput() {
  }

  public OrderInput(String orderId, long totalCents, String currency, List<OrderItem> items, FailStep failStep) {
    this.orderId = orderId;
    this.totalCents = totalCents;
    this.currency = currency;
    this.items = items;
    this.failStep = failStep;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public long getTotalCents() {
    return totalCents;
  }

  public void setTotalCents(long totalCents) {
    this.totalCents = totalCents;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
    this.items = items;
  }

  public FailStep getFailStep() {
    return failStep;
  }

  public void setFailStep(FailStep failStep) {
    this.failStep = failStep;
  }
}
