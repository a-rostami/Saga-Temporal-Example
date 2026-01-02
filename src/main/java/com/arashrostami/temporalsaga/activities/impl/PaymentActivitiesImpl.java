package com.arashrostami.temporalsaga.activities.impl;

import com.arashrostami.temporalsaga.activities.PaymentActivities;
import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.IdempotencyService;
import io.temporal.spring.boot.ActivityImpl;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Simulated charge/refund with forced failure
@Component("paymentActivitiesImpl")
@ActivityImpl(workers = "orderWorker")
public class PaymentActivitiesImpl implements PaymentActivities {
  private static final Logger log = LoggerFactory.getLogger(PaymentActivitiesImpl.class);

  private final IdempotencyService idempotencyService;

  public PaymentActivitiesImpl(IdempotencyService idempotencyService) {
    this.idempotencyService = idempotencyService;
  }

  @Override
  public String charge(String orderId, long totalCents, String currency, String idempotencyKey, FailStep failStep) {
    if (failStep == FailStep.PAYMENT) {
      log.info("saga_id={} payment fail simulated", orderId);
      throw new RuntimeException("simulated payment failure");
    }

    return idempotencyService.findResult(idempotencyKey)
        .map(existing -> {
          log.info("saga_id={} payment already charged", orderId);
          return existing;
        })
        .orElseGet(() -> {
          String paymentId = "pay-" + UUID.randomUUID();
          idempotencyService.saveResult(idempotencyKey, paymentId);
          log.info("saga_id={} payment charged amount={} {}", orderId, totalCents, currency);
          return paymentId;
        });
  }

  @Override
  public void refund(String orderId, String paymentId, String idempotencyKey) {
    if (idempotencyService.exists(idempotencyKey)) {
      log.info("saga_id={} refund skipped", orderId);
      return;
    }

    idempotencyService.saveResult(idempotencyKey, "OK");
    log.info("saga_id={} payment refunded", orderId);
  }
}
