package com.arashrostami.temporalsaga.domain;

import com.arashrostami.temporalsaga.persistence.OrderEntity;
import com.arashrostami.temporalsaga.persistence.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Small service for order updates
@Service
public class OrderService {
  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  private final OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional
  public OrderEntity createOrder(String orderId, long totalCents, String currency) {
    Optional<OrderEntity> existing = orderRepository.findById(orderId);
    if (existing.isPresent()) {
      return existing.get();
    }

    OrderEntity order = new OrderEntity();
    order.setId(orderId);
    order.setStatus(OrderStatus.PENDING);
    order.setTotalAmount(totalCents);
    order.setCurrency(currency);
    OrderEntity saved = orderRepository.save(order);
    log.info("saga_id={} order created", orderId);
    return saved;
  }

  @Transactional
  public void setWorkflowRunId(String orderId, String runId) {
    OrderEntity order = requireOrder(orderId);
    order.setWorkflowRunId(runId);
    orderRepository.save(order);
    log.info("saga_id={} workflow runId saved", orderId);
  }

  @Transactional
  public void setStatus(String orderId, OrderStatus status) {
    OrderEntity order = requireOrder(orderId);
    order.setStatus(status);
    orderRepository.save(order);
    log.info("saga_id={} status={}", orderId, status);
  }

  @Transactional
  public void attachInventory(String orderId, String reservationId) {
    OrderEntity order = requireOrder(orderId);
    if (order.getInventoryReservationId() == null) {
      order.setInventoryReservationId(reservationId);
    }
    order.setStatus(OrderStatus.INVENTORY_RESERVED);
    orderRepository.save(order);
    log.info("saga_id={} inventory reserved", orderId);
  }

  @Transactional
  public void attachPayment(String orderId, String paymentId) {
    OrderEntity order = requireOrder(orderId);
    if (order.getPaymentId() == null) {
      order.setPaymentId(paymentId);
    }
    order.setStatus(OrderStatus.PAYMENT_CHARGED);
    orderRepository.save(order);
    log.info("saga_id={} payment charged", orderId);
  }

  @Transactional
  public void attachShipment(String orderId, String shipmentId) {
    OrderEntity order = requireOrder(orderId);
    if (order.getShipmentId() == null) {
      order.setShipmentId(shipmentId);
    }
    order.setStatus(OrderStatus.SHIPPING_CREATED);
    orderRepository.save(order);
    log.info("saga_id={} shipment created", orderId);
  }

  @Transactional(readOnly = true)
  public OrderEntity getOrder(String orderId) {
    return requireOrder(orderId);
  }

  private OrderEntity requireOrder(String orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("order not found"));
  }
}
