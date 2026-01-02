package com.arashrostami.temporalsaga.api;

import com.arashrostami.temporalsaga.domain.FailStep;
import com.arashrostami.temporalsaga.domain.OrderInput;
import com.arashrostami.temporalsaga.domain.OrderService;
import com.arashrostami.temporalsaga.persistence.OrderEntity;
import com.arashrostami.temporalsaga.workflow.OrderSagaWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Orders API
@RestController
@RequestMapping("/orders")
public class OrderController {
  private static final String TASK_QUEUE = "order-saga-tq";

  private final WorkflowClient workflowClient;
  private final OrderService orderService;

  public OrderController(WorkflowClient workflowClient, OrderService orderService) {
    this.workflowClient = workflowClient;
    this.orderService = orderService;
  }

  @PostMapping
  public OrderCreateResponse createOrder(@Valid @RequestBody OrderCreateRequest request) {
    String orderId = UUID.randomUUID().toString();
    FailStep failStep = request.getFailStep() == null ? FailStep.NONE : request.getFailStep();

    orderService.createOrder(orderId, request.getTotalCents(), request.getCurrency());

    OrderInput input = new OrderInput(
        orderId,
        request.getTotalCents(),
        request.getCurrency(),
        request.getItems(),
        failStep
    );

    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setWorkflowId(orderId)
        .setTaskQueue(TASK_QUEUE)
        .build();

    OrderSagaWorkflow workflow = workflowClient.newWorkflowStub(OrderSagaWorkflow.class, options);
    WorkflowExecution execution = WorkflowClient.start(workflow::process, input);

    orderService.setWorkflowRunId(orderId, execution.getRunId());

    return new OrderCreateResponse(orderId, execution.getRunId());
  }

  @GetMapping("/{orderId}")
  public OrderResponse getOrder(@PathVariable String orderId) {
    OrderEntity order = orderService.getOrder(orderId);
    return new OrderResponse(
        order.getId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getCurrency(),
        order.getInventoryReservationId(),
        order.getPaymentId(),
        order.getShipmentId(),
        order.getWorkflowRunId(),
        order.getCreatedAt(),
        order.getUpdatedAt()
    );
  }

  @GetMapping("/{orderId}/workflow")
  public OrderWorkflowResponse getWorkflow(@PathVariable String orderId) {
    OrderEntity order = orderService.getOrder(orderId);
    return new OrderWorkflowResponse(order.getId(), order.getId(), order.getWorkflowRunId());
  }
}
