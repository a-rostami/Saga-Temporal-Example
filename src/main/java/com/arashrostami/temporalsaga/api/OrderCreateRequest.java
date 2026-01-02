package com.arashrostami.temporalsaga.api;

import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

// Order create request
public class OrderCreateRequest {
  @Min(1)
  private long totalCents;

  @NotBlank
  private String currency;

  @Valid
  @NotEmpty
  private List<OrderItem> items;

  private FailStep failStep;

  public OrderCreateRequest() {
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
