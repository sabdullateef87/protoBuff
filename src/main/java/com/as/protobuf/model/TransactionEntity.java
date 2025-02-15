package com.as.protobuf.model;

import com.as.protobuf.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String accountId;
  private Double amount;
  private String currency;
  @Enumerated(EnumType.STRING)
  private TransactionType type;
  private String description;
  private Instant timestamp;
  private String status;
}
