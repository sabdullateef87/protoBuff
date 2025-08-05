package com.as.protobuf.messaging;

import com.as.protobuf.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {
  private final TransactionService transactionService;


//  @KafkaListener(topics = "transactions", groupId = "transaction-group")
//  @MeasureProcessingTime
//  public void consume(byte[] message) {
//    try {
//      Transaction transaction = Transaction.parseFrom(message);
//      transactionService.processTransaction(transaction);
//    } catch (InvalidProtocolBufferException e) {
//      log.error("Error parsing protobuf message", e);
//    }
//  }
}
