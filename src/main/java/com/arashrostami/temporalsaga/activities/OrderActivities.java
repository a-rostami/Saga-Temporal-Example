package com.arashrostami.temporalsaga.activities;

import com.arashrostami.temporalsaga.domain.OrderStatus;
import io.temporal.activity.ActivityInterface;

// Activities for order updates
@ActivityInterface
public interface OrderActivities {
  void setStatus(String orderId, OrderStatus status);

  void attachInventory(String orderId, String reservationId);

  void attachPayment(String orderId, String paymentId);

  void attachShipment(String orderId, String shipmentId);
}
