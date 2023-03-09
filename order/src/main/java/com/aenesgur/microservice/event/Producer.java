package com.aenesgur.microservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Producer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String key, String message){
        kafkaTemplate.send(topic, key, message);
        log.info("Message added to queue with key: {} and data: {}", key, message);
    }
}
