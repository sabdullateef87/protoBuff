package com.as.protobuf.messaging;

import com.as.proto.Transaction;
import com.as.protobuf.annotations.MeasureProcessingTime;
import com.as.protobuf.service.TransactionService;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
