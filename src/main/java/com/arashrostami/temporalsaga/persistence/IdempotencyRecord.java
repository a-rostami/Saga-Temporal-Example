package com.arashrostami.temporalsaga.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

// Idempotency records for activities
@Entity
@Table(name = "idempotency_records")
public class IdempotencyRecord {
  @Id
  @Column(name = "idempotency_key")
  private String key;

  private String result;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public IdempotencyRecord() {
  }

  public IdempotencyRecord(String key, String result) {
    this.key = key;
    this.result = result;
  }

  @PrePersist
  public void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = Instant.now();
    }
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
