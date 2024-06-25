package com.example.Producer.Producer.controller;

import com.example.Producer.Common.ProductMessage;
import com.example.Producer.Producer.service.MessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrderController {

    private final MessageProducer messageProducer;

    public OrderController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody ProductMessage message) {
        try {
            messageProducer.sendAsyncMessage(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Message Send to Consumer: {}", message);
        return ResponseEntity.ok("Message sent!");
    }
}
