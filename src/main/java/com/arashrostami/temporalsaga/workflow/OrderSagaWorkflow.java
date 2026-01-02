package com.arashrostami.temporalsaga.workflow;

import com.arashrostami.temporalsaga.domain.OrderInput;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

// Main orchestration entry
@WorkflowInterface
public interface OrderSagaWorkflow {
  @WorkflowMethod
  void process(OrderInput input);
}
