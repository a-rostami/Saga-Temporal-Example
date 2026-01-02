package com.arashrostami.temporalsaga.activities;

import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.OrderItem;
import io.temporal.activity.ActivityInterface;
import java.util.List;

// Inventory activities
@ActivityInterface
public interface InventoryActivities {
  String reserve(String orderId, List<OrderItem> items, String idempotencyKey, FailStep failStep);

  void release(String orderId, String reservationId, String idempotencyKey);
}
