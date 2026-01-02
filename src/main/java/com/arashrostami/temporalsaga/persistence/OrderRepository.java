package com.arashrostami.temporalsaga.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

// Order repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
