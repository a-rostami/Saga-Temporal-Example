package com.arashrostami.temporalsaga.workflow.impl;

import com.arashrostami.temporalsaga.activities.InventoryActivities;
import com.arashrostami.temporalsaga.activities.OrderActivities;
import com.arashrostami.temporalsaga.activities.PaymentActivities;
import com.arashrostami.temporalsaga.activities.ShippingActivities;
import com.arashrostami.temporalsaga.domain.OrderInput;
import com.arashrostami.temporalsaga.domain.OrderStatus;
import com.arashrostami.temporalsaga.workflow.OrderSagaWorkflow;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import io.temporal.spring.boot.WorkflowImpl;
import java.time.Duration;
import org.slf4j.Logger;

// Main order saga with Temporal
@WorkflowImpl(workers = "orderWorker")
public class OrderSagaWorkflowImpl implements OrderSagaWorkflow {
  private static final Logger log = Workflow.getLogger(OrderSagaWorkflowImpl.class);

  private enum Step {
    NONE,
    INVENTORY,
    PAYMENT,
    SHIPPING
  }

  @Override
  public void process(OrderInput input) {
    String orderId = input.getOrderId();
    log.info("saga_id={} saga started", orderId);

    RetryOptions retryOptions = RetryOptions.newBuilder()
        .setInitialInterval(Duration.ofSeconds(1))
        .setMaximumAttempts(3)
        .build();

    var activityOptions = io.temporal.activity.ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(10))
        .setRetryOptions(retryOptions)
        .build();

    InventoryActivities inventoryActivities = Workflow.newActivityStub(InventoryActivities.class, activityOptions);
    PaymentActivities paymentActivities = Workflow.newActivityStub(PaymentActivities.class, activityOptions);
    ShippingActivities shippingActivities = Workflow.newActivityStub(ShippingActivities.class, activityOptions);
    OrderActivities orderActivities = Workflow.newActivityStub(OrderActivities.class, activityOptions);

    Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
    Step step = Step.NONE;

    try {
      step = Step.INVENTORY;
      String reservationId = inventoryActivities.reserve(
          orderId,
          input.getItems(),
          key(orderId, "INVENTORY_RESERVE"),
          input.getFailStep()
      );
      orderActivities.attachInventory(orderId, reservationId);
      saga.addCompensation(() -> inventoryActivities.release(
          orderId,
          reservationId,
          key(orderId, "INVENTORY_RELEASE")
      ));

      step = Step.PAYMENT;
      String paymentId = paymentActivities.charge(
          orderId,
          input.getTotalCents(),
          input.getCurrency(),
          key(orderId, "PAYMENT_CHARGE"),
          input.getFailStep()
      );
      orderActivities.attachPayment(orderId, paymentId);
      saga.addCompensation(() -> paymentActivities.refund(
          orderId,
          paymentId,
          key(orderId, "PAYMENT_REFUND")
      ));

      step = Step.SHIPPING;
      String shipmentId = shippingActivities.createShipment(
          orderId,
          input.getItems(),
          "mock-address",
          key(orderId, "SHIPPING_CREATE"),
          input.getFailStep()
      );
      orderActivities.attachShipment(orderId, shipmentId);
      saga.addCompensation(() -> shippingActivities.cancelShipment(
          orderId,
          shipmentId,
          key(orderId, "SHIPPING_CANCEL")
      ));

      orderActivities.setStatus(orderId, OrderStatus.CONFIRMED);
      log.info("saga_id={} saga completed", orderId);
    } catch (Exception e) {
      log.warn("saga_id={} saga failed: {}", orderId, e.getMessage());
      orderActivities.setStatus(orderId, OrderStatus.COMPENSATING);
      // If it fails, run compensation
      saga.compensate();

      if (step == Step.PAYMENT) {
        orderActivities.setStatus(orderId, OrderStatus.FAILED_PAYMENT);
      } else if (step == Step.SHIPPING) {
        orderActivities.setStatus(orderId, OrderStatus.FAILED_SHIPPING);
      } else {
        orderActivities.setStatus(orderId, OrderStatus.FAILED_PAYMENT);
      }
    }
  }

  private String key(String orderId, String action) {
    return orderId + ":" + action;
  }
}
