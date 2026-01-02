package com.arashrostami.temporalsaga.domain;

// Order states in the saga
public enum OrderStatus {
  PENDING,
  INVENTORY_RESERVED,
  PAYMENT_CHARGED,
  SHIPPING_CREATED,
  CONFIRMED,
  FAILED_PAYMENT,
  FAILED_SHIPPING,
  COMPENSATING
}
