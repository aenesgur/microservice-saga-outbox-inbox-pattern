package com.aenesgur.microservice.service.scheduledJob;

import com.aenesgur.microservice.event.Producer;
import com.aenesgur.microservice.model.entity.OrderOutbox;
import com.aenesgur.microservice.repository.OrderOutboxRepository;
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

    @Value("${queue.kafka.topic.order-create-topic}")
    private String ORDER_CREATE_TOPIC;

    @Value("${outbox.lock.key}")
    private String OUTBOX_LOCK_KEY;

    private final Producer producer;

    private final OrderOutboxRepository orderOutboxRepository;

    @Scheduled(fixedDelayString = "${outbox.polling.interval}")
    public void pollOutbox() {
        RLock lock = redissonClient.getLock(OUTBOX_LOCK_KEY);
        boolean isLocked = false;

        try{
            // Try to acquire the lock
            isLocked = lock.tryLock(10, TimeUnit.SECONDS);
            if (isLocked) {
                log.info("Lock acquired for outbox processing");
                List<OrderOutbox> orderOutboxes = orderOutboxRepository.findAll(PageRequest.of(0, 2)).toList();
                for(OrderOutbox orderOutbox:orderOutboxes){
                    producer.sendMessage(ORDER_CREATE_TOPIC, orderOutbox.getIdempotentToken(), orderOutbox.getPayload());
                    orderOutboxRepository.deleteById(orderOutbox.getId());
                    log.info("Order deleted from outbox by id: {}", orderOutbox.getId());
                }
            }else{
                // Failed to acquire the lock
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
}
