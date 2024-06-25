package com.example.Producer.Producer.controller;

import com.example.Producer.Common.ProductMessage;
import com.example.Producer.Producer.service.RpcMessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class RpcClientController {
    private final RpcMessageProducer rpcMessageProducer;

    public RpcClientController(RpcMessageProducer rpcMessageProducer) {
        this.rpcMessageProducer = rpcMessageProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<Object> sendRequest(@RequestBody ProductMessage message) {
        try {
            ProductMessage responseMessage = rpcMessageProducer.sendRpcMessage(message);
            log.info("Message send to Consumer: {}", message);
            log.info("Response received from Consumer: {}", responseMessage);
            return ResponseEntity.ok(responseMessage);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error processing message: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(504).body("Request timed out or no response received");
        }
    }
}
