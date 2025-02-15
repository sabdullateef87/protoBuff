package com.as.protobuf.controller;

import com.as.protobuf.messaging.TransactionProducer;
import com.as.protobuf.model.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController {
  private final TransactionProducer transactionProducer;

  @GetMapping("/transactions")
  public ResponseEntity<String> createTransaction(@Param("numberOfMessages") Long numberOfMessages) {
    transactionProducer.publishTransaction(numberOfMessages);
    return ResponseEntity.accepted().build();
  }
}
