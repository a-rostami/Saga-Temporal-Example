package com.arashrostami.temporalsaga.api;

// Order create response
public class OrderCreateResponse {
  private final String orderId;
  private final String workflowRunId;

  public OrderCreateResponse(String orderId, String workflowRunId) {
    this.orderId = orderId;
    this.workflowRunId = workflowRunId;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getWorkflowRunId() {
    return workflowRunId;
  }
}
