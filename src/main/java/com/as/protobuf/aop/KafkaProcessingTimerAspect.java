package com.as.protobuf.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Aspect
@Component
@Slf4j
public class KafkaProcessingTimerAspect {
  private final AtomicLong messageCount = new AtomicLong(0);
  private final AtomicReference<Long> startTime = new AtomicReference<>(null);
  private static final long TARGET_MESSAGE_COUNT = 10_000;

  @Around("@annotation(com.as.protobuf.annotations.MeasureProcessingTime)")
  public Object measureProcessingTime(ProceedingJoinPoint joinPoint) throws Throwable {
    startTime.compareAndSet(null, System.currentTimeMillis());

    Object result = joinPoint.proceed();

    long currentCount = messageCount.incrementAndGet();

    if (currentCount == TARGET_MESSAGE_COUNT) {
      long totalTime = System.currentTimeMillis() - startTime.get();
      double messagesPerSecond = (double) TARGET_MESSAGE_COUNT / (totalTime / 1000.0);

      log.info("Processing completed:");
      log.info("Total messages processed: {}", TARGET_MESSAGE_COUNT);
      log.info("Total time taken: {} ms", totalTime);
      log.info("Average throughput: {} messages/second", String.format("%.2f", messagesPerSecond));

      // Reset counters
      messageCount.set(0);
      startTime.set(null);
    }

    return result;
  }
}