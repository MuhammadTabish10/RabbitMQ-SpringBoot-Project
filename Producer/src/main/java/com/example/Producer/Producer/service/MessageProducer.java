package com.example.Producer.Producer.service;

import com.example.Producer.Common.ProductMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final DirectExchange asyncExchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public MessageProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, @Qualifier("asyncExchange") DirectExchange asyncExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.asyncExchange = asyncExchange;
    }

    public void sendAsyncMessage(ProductMessage message) throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(message);
        rabbitTemplate.convertAndSend(asyncExchange.getName(), routingKey, jsonMessage);
    }
}
