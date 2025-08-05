package com.as.protobuf.model.request;

import com.as.protobuf.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class TransactionRequest implements Serializable {
  private String accountId;
  private Double amount;
  private String currency;
  private TransactionType type;
  private String description;
  private Instant timestamp;
  private String status;
}
