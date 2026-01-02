package com.arashrostami.temporalsaga.domain;

import com.arashrostami.temporalsaga.persistence.IdempotencyRecord;
import com.arashrostami.temporalsaga.persistence.IdempotencyRecordRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Simple idempotency key service
@Service
public class IdempotencyService {
  private final IdempotencyRecordRepository repository;

  public IdempotencyService(IdempotencyRecordRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public Optional<String> findResult(String key) {
    return repository.findById(key).map(IdempotencyRecord::getResult);
  }

  @Transactional(readOnly = true)
  public boolean exists(String key) {
    return repository.existsById(key);
  }

  @Transactional
  public void saveResult(String key, String result) {
    repository.save(new IdempotencyRecord(key, result));
  }
}
