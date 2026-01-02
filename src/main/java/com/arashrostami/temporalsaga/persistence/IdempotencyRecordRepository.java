package com.arashrostami.temporalsaga.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

// Idempotency key repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, String> {
}
