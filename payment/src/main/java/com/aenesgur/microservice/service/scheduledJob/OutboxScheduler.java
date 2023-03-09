package com.aenesgur.microservice.service.scheduledJob;

import com.aenesgur.microservice.event.Producer;
import com.aenesgur.microservice.event.enums.PaymentResultStatus;
import com.aenesgur.microservice.model.entity.PaymentOutbox;
import com.aenesgur.microservice.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {
    private final RedissonClient redissonClient;

    @Value("${kafka.topic.payment-fail-topic}")
    private String PAYMENT_FAIL_TOPIC;

    @Value("${kafka.topic.payment-complete-topic}")
    private String PAYMENT_COMPLETE_TOPIC;

    @Value("${outbox.lock.key}")
    private String OUTBOX_LOCK_KEY;

    private final Producer producer;

    private final PaymentOutboxRepository paymentOutboxRepository;

    @Scheduled(fixedDelayString = "${outbox.polling.interval}")
    public void pollOutbox() {
        RLock lock = redissonClient.getLock(OUTBOX_LOCK_KEY);
        boolean isLocked = false;

        try{
            isLocked = lock.tryLock(10, TimeUnit.SECONDS);
            if (isLocked) {
                log.info("Lock acquired for outbox processing");
                List<PaymentOutbox> paymentOutboxes = paymentOutboxRepository.findAll(PageRequest.of(0, 2)).toList();
                for(PaymentOutbox paymentOutbox:paymentOutboxes){
                    sendQueue(paymentOutbox);
                    paymentOutboxRepository.deleteById(paymentOutbox.getId());
                    log.info("Order deleted from outbox by id: {}", paymentOutbox.getId());
                }
            }else{
                log.info("Failed to acquire lock for outbox processing.");
            }
        }
        catch (InterruptedException exception){
            log.error("InterruptedException happened: {}", exception);
            Thread.currentThread().interrupt();
        }
        finally {
            if (isLocked) {
                lock.unlock();
                log.info("Lock released for outbox processing.");
            }
        }
    }

    private void sendQueue(PaymentOutbox paymentOutbox){
        String topic = paymentOutbox.getPaymentStatus().name().equals(PaymentResultStatus.PAID.name()) ? PAYMENT_COMPLETE_TOPIC : PAYMENT_FAIL_TOPIC;
        producer.sendMessage(topic, paymentOutbox.getIdempotentToken(), paymentOutbox.getPayload());
    }
}
