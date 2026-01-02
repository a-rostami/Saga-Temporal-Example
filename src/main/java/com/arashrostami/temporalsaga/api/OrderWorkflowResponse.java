package com.arashrostami.temporalsaga.api;

// Workflow summary response
public class OrderWorkflowResponse {
  private final String orderId;
  private final String workflowId;
  private final String workflowRunId;

  public OrderWorkflowResponse(String orderId, String workflowId, String workflowRunId) {
    this.orderId = orderId;
    this.workflowId = workflowId;
    this.workflowRunId = workflowRunId;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  public String getWorkflowRunId() {
    return workflowRunId;
  }
}
