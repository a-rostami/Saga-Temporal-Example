package com.arashrostami.temporalsaga.activities;

import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.OrderItem;
import io.temporal.activity.ActivityInterface;
import java.util.List;

// Shipping activities
@ActivityInterface
public interface ShippingActivities {
  String createShipment(String orderId, List<OrderItem> items, String address, String idempotencyKey, FailStep failStep);

  void cancelShipment(String orderId, String shipmentId, String idempotencyKey);
}
