package com.as.protobuf.messaging;

import com.as.proto.Transaction;
import com.as.protobuf.annotations.MeasureProcessingTime;
import com.as.protobuf.enums.TransactionType;
import com.as.protobuf.model.TransactionEntity;
import com.as.protobuf.service.TransactionService;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionBatchConsumer {
  private static final int BATCH_SIZE = 10000;

  private final ConcurrentLinkedQueue<Transaction> transactionBuffer = new ConcurrentLinkedQueue<>();
  private final TransactionService transactionService;

  @KafkaListener(topics = "transactions", groupId = "transaction-group",  containerFactory = "kafkaListenerContainerFactory")
  @MeasureProcessingTime
  public void consume(byte[] transactionMessageByte, Acknowledgment acknowledgment) throws InvalidProtocolBufferException {
    transactionBuffer.add(Transaction.parseFrom(transactionMessageByte));

    if (transactionBuffer.size() >= BATCH_SIZE) {
      List<TransactionEntity> batch = new ArrayList<>();
      while (batch.size() < BATCH_SIZE && !transactionBuffer.isEmpty()) {
        batch.add(convertToEntity(transactionBuffer.poll()));
      }
      processBatch(batch);
      acknowledgment.acknowledge();
    }
  }

  private void processBatch(List<TransactionEntity> batch) {
    try {
      transactionService.copyTransactions(batch);
    } catch (Exception e) {
      log.error("{}", String.valueOf(e));
    }
  }

  @Scheduled(fixedRate = 5000)
  public void flushRemainingTransactions() {
    if (!transactionBuffer.isEmpty()) {
      List<TransactionEntity> batch = new ArrayList<>();
      while (!transactionBuffer.isEmpty()) {
        batch.add(convertToEntity(transactionBuffer.poll()));
      }
      processBatch(batch);
    }
  }


  private TransactionEntity convertToEntity(Transaction proto) {
    TransactionEntity entity = new TransactionEntity();
    entity.setAccountId(proto.getAccountId());
    entity.setAmount(proto.getAmount());
    entity.setCurrency(proto.getCurrency());
    entity.setType(TransactionType.valueOf(proto.getType().name()));
    entity.setDescription(proto.getDescription());
    entity.setTimestamp(Instant.ofEpochMilli(proto.getTimestamp()));
    entity.setStatus(proto.getStatus());
    return entity;
  }

}
