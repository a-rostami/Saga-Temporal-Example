package com.arashrostami.temporalsaga.activities;

import com.arashrostami.temporalsaga.domain.FailStep;
import io.temporal.activity.ActivityInterface;

// Payment activities
@ActivityInterface
public interface PaymentActivities {
  String charge(String orderId, long totalCents, String currency, String idempotencyKey, FailStep failStep);

  void refund(String orderId, String paymentId, String idempotencyKey);
}
