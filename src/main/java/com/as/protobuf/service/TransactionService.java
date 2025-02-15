package com.as.protobuf.service;

import com.as.protobuf.enums.TransactionType;
import com.as.protobuf.model.TransactionEntity;
import com.as.protobuf.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.as.proto.Transaction;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final DataSource dataSource;

  @Transactional
  public void processTransaction(Transaction protoTransaction) {
    TransactionEntity entity = convertToEntity(protoTransaction);
    transactionRepository.save(entity);
  }


  @Transactional
  public void copyTransactions(List<TransactionEntity> transactions) {
    StringBuilder csvData = new StringBuilder();

    // Convert transactions to CSV format
    for (TransactionEntity transaction : transactions) {
      csvData.append(transaction.getAccountId()).append(",")
          .append(transaction.getAmount()).append(",")
          .append(transaction.getCurrency()).append(",")
          .append(transaction.getType()).append(",")
          .append(transaction.getDescription()).append(",")
          .append(transaction.getTimestamp()).append(",")
          .append(transaction.getStatus()).append("\n");
    }


    // Perform the COPY operation
    try (Connection connection = dataSource.getConnection()) {
      BaseConnection baseConnection = connection.unwrap(BaseConnection.class);
      CopyManager copyManager = new CopyManager(baseConnection);
      try (ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.toString().getBytes())) {
        copyManager.copyIn(
            "COPY transactions (account_id, amount, currency, type, description, timestamp, status) FROM STDIN WITH CSV",
            inputStream
        );
      }
    } catch (Exception e) {
      log.error("Error during COPY operation: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to copy transactions to PostgreSQL", e);
    }
  }

  private TransactionEntity convertToEntity(Transaction proto) {
    TransactionEntity entity = new TransactionEntity();
    entity.setAccountId(proto.getAccountId());
    entity.setAmount(proto.getAmount());
    entity.setCurrency(proto.getCurrency());
    entity.setType(TransactionType.valueOf(proto.getType().name()));
    entity.setDescription(proto.getDescription());
    entity.setTimestamp(Instant.now());
    entity.setStatus(proto.getStatus());
    return entity;
  }
}
