package com.as.protobuf.messaging;

import com.as.proto.Transaction;

import com.as.proto.TransactionType;
import com.as.protobuf.model.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.as.protobuf.enums.*;
@Component
public class TransactionProducer {
  private static final String TOPIC = "transactions";
  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  public TransactionProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }


  public void publishTransaction(long numberOfMessages) {
    Transaction transaction = generateTransaction();
    byte[] messageBytes = transaction.toByteArray();
    LongStream.range(0, numberOfMessages)
        .forEach(i -> kafkaTemplate.send(TOPIC, transaction.getAccountId(), messageBytes));
  }


  public static Transaction generateTransaction() {
    Random random = new Random();
    return Transaction.newBuilder()
        .setTransactionId(UUID.randomUUID().toString())
        .setAccountId(UUID.randomUUID().toString())
        .setAmount(100 + (5000 - 100) * random.nextDouble())
        .setCurrency(randomCurrency())
        .setType(random.nextBoolean() ? TransactionType.CREDIT : TransactionType.DEBIT)
        .setDescription("Sample transaction description")
        .setTimestamp(System.currentTimeMillis())
        .setStatus(random.nextBoolean() ? "COMPLETED" : "PENDING")
                .build();
  }

  private static String randomCurrency() {
    String[] currencies = {"USD", "EUR", "GBP", "JPY", "INR"};
    return currencies[new Random().nextInt(currencies.length)];
  }
}