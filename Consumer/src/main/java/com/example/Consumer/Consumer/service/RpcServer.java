package com.example.Consumer.Consumer.service;

import com.example.Consumer.Common.ProductMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpcServer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.rpc.queue}")
    public String processRequest(String message) {
        try {
            ProductMessage requestMessage = objectMapper.readValue(message, ProductMessage.class);
            log.info("Received request: {}", requestMessage);

            requestMessage.setDescription("Processed " + requestMessage.getDescription());
            String response = objectMapper.writeValueAsString(requestMessage);
            log.info("Sending response: {}", response);
            return response;

        } catch (JsonProcessingException e) {
            log.error("Error processing request", e);
            return null;
        }
    }
}
