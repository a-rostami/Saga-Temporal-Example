package com.arashrostami.temporalsaga.activities.impl;

import com.arashrostami.temporalsaga.activities.ShippingActivities;
import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.IdempotencyService;
import com.arashrostami.temporalsaga.domain.OrderItem;
import io.temporal.spring.boot.ActivityImpl;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Simulated create/cancel shipment
@Component("shippingActivitiesImpl")
@ActivityImpl(workers = "orderWorker")
public class ShippingActivitiesImpl implements ShippingActivities {
  private static final Logger log = LoggerFactory.getLogger(ShippingActivitiesImpl.class);

  private final IdempotencyService idempotencyService;

  public ShippingActivitiesImpl(IdempotencyService idempotencyService) {
    this.idempotencyService = idempotencyService;
  }

  @Override
  public String createShipment(String orderId, List<OrderItem> items, String address, String idempotencyKey, FailStep failStep) {
    if (failStep == FailStep.SHIPPING) {
      log.info("saga_id={} shipping fail simulated", orderId);
      throw new RuntimeException("simulated shipping failure");
    }

    return idempotencyService.findResult(idempotencyKey)
        .map(existing -> {
          log.info("saga_id={} shipment already created", orderId);
          return existing;
        })
        .orElseGet(() -> {
          String shipmentId = "ship-" + UUID.randomUUID();
          idempotencyService.saveResult(idempotencyKey, shipmentId);
          log.info("saga_id={} shipment created items={} addr={}", orderId, items.size(), address);
          return shipmentId;
        });
  }

  @Override
  public void cancelShipment(String orderId, String shipmentId, String idempotencyKey) {
    if (idempotencyService.exists(idempotencyKey)) {
      log.info("saga_id={} cancel shipment skipped", orderId);
      return;
    }

    idempotencyService.saveResult(idempotencyKey, "OK");
    log.info("saga_id={} shipment canceled", orderId);
  }
}
