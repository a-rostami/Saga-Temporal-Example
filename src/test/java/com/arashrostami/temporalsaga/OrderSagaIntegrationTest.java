package com.arashrostami.temporalsaga;

import static org.assertj.core.api.Assertions.assertThat;

import com.arashrostami.temporalsaga.activities.impl.InventoryActivitiesImpl;
import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.OrderInput;
import com.arashrostami.temporalsaga.domain.OrderItem;
import com.arashrostami.temporalsaga.domain.OrderService;
import com.arashrostami.temporalsaga.domain.OrderStatus;
import com.arashrostami.temporalsaga.persistence.OrderRepository;
import com.arashrostami.temporalsaga.workflow.OrderSagaWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Happy + fail tests for saga
@SpringBootTest
@ActiveProfiles("test")
class OrderSagaIntegrationTest {
  private static final String TASK_QUEUE = "order-saga-tq";

  @Autowired
  private WorkflowClient workflowClient;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private InventoryActivitiesImpl inventoryActivities;

  @Test
  void paymentFailure_compensatesInventory() {
    String orderId = UUID.randomUUID().toString();
    orderService.createOrder(orderId, 2599, "GBP");

    OrderInput input = new OrderInput(
        orderId,
        2599,
        "GBP",
        List.of(new OrderItem("SKU-1", 2)),
        FailStep.PAYMENT
    );

    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setWorkflowId(orderId)
        .setTaskQueue(TASK_QUEUE)
        .build();

    OrderSagaWorkflow workflow = workflowClient.newWorkflowStub(OrderSagaWorkflow.class, options);
    workflow.process(input);

    var order = orderRepository.findById(orderId).orElseThrow();
    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED_PAYMENT);
    assertThat(inventoryActivities.hasReservation(orderId)).isFalse();
  }

  @Test
  void happyPath_confirmsOrder() {
    String orderId = UUID.randomUUID().toString();
    orderService.createOrder(orderId, 1999, "GBP");

    OrderInput input = new OrderInput(
        orderId,
        1999,
        "GBP",
        List.of(new OrderItem("SKU-1", 1)),
        FailStep.NONE
    );

    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setWorkflowId(orderId)
        .setTaskQueue(TASK_QUEUE)
        .build();

    OrderSagaWorkflow workflow = workflowClient.newWorkflowStub(OrderSagaWorkflow.class, options);
    workflow.process(input);

    var order = orderRepository.findById(orderId).orElseThrow();
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
  }
}
