package com.arashrostami.temporalsaga.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Order item, sku + qty
public class OrderItem {
  @NotBlank
  private String sku;

  @Min(1)
  private int qty;

  public OrderItem() {
  }

  public OrderItem(String sku, int qty) {
    this.sku = sku;
    this.qty = qty;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public int getQty() {
    return qty;
  }

  public void setQty(int qty) {
    this.qty = qty;
  }
}
