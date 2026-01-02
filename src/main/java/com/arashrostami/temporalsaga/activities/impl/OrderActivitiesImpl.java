package com.arashrostami.temporalsaga.activities.impl;

import com.arashrostami.temporalsaga.activities.OrderActivities;
import com.arashrostami.temporalsaga.domain.OrderService;
import com.arashrostami.temporalsaga.domain.OrderStatus;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Simple bridge between workflow and DB
@Component("orderActivitiesImpl")
@ActivityImpl(workers = "orderWorker")
public class OrderActivitiesImpl implements OrderActivities {
  private static final Logger log = LoggerFactory.getLogger(OrderActivitiesImpl.class);

  private final OrderService orderService;

  public OrderActivitiesImpl(OrderService orderService) {
    this.orderService = orderService;
  }

  @Override
  public void setStatus(String orderId, OrderStatus status) {
    orderService.setStatus(orderId, status);
    log.info("saga_id={} status set to {}", orderId, status);
  }

  @Override
  public void attachInventory(String orderId, String reservationId) {
    orderService.attachInventory(orderId, reservationId);
    log.info("saga_id={} inventory attached", orderId);
  }

  @Override
  public void attachPayment(String orderId, String paymentId) {
    orderService.attachPayment(orderId, paymentId);
    log.info("saga_id={} payment attached", orderId);
  }

  @Override
  public void attachShipment(String orderId, String shipmentId) {
    orderService.attachShipment(orderId, shipmentId);
    log.info("saga_id={} shipment attached", orderId);
  }
}
