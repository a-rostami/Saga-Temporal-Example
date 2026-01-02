package com.arashrostami.temporalsaga.activities.impl;

import com.arashrostami.temporalsaga.activities.InventoryActivities;
import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.IdempotencyService;
import com.arashrostami.temporalsaga.domain.OrderItem;
import io.temporal.spring.boot.ActivityImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Simulated inventory with basic idempotency
@Component("inventoryActivitiesImpl")
@ActivityImpl(workers = "orderWorker")
public class InventoryActivitiesImpl implements InventoryActivities {
  private static final Logger log = LoggerFactory.getLogger(InventoryActivitiesImpl.class);

  private final IdempotencyService idempotencyService;
  private final Map<String, String> reservations = new ConcurrentHashMap<>();

  public InventoryActivitiesImpl(IdempotencyService idempotencyService) {
    this.idempotencyService = idempotencyService;
  }

  @Override
  public String reserve(String orderId, List<OrderItem> items, String idempotencyKey, FailStep failStep) {
    return idempotencyService.findResult(idempotencyKey)
        .map(existing -> {
          reservations.putIfAbsent(orderId, existing);
          log.info("saga_id={} inventory already reserved", orderId);
          return existing;
        })
        .orElseGet(() -> {
          String reservationId = "resv-" + UUID.randomUUID();
          reservations.put(orderId, reservationId);
          idempotencyService.saveResult(idempotencyKey, reservationId);
          log.info("saga_id={} inventory reserved items={}", orderId, items.size());
          return reservationId;
        });
  }

  @Override
  public void release(String orderId, String reservationId, String idempotencyKey) {
    if (idempotencyService.exists(idempotencyKey)) {
      reservations.remove(orderId);
      log.info("saga_id={} inventory release skipped", orderId);
      return;
    }

    reservations.remove(orderId);
    idempotencyService.saveResult(idempotencyKey, "OK");
    log.info("saga_id={} inventory released", orderId);
  }

  public boolean hasReservation(String orderId) {
    return reservations.containsKey(orderId);
  }
}
