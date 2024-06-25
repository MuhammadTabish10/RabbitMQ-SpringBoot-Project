package com.example.Producer.Producer.service;

import com.example.Producer.Common.ProductMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RpcMessageProducer {

    public static final String AMQ_RABBITMQ_REPLY_TO = "amq.rabbitmq.reply-to";

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange rpcExchange;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.rpc.routing.key}")
    private String rpcRoutingKey;

    public RpcMessageProducer(RabbitTemplate rabbitTemplate,
                              @Qualifier("rpcExchange") DirectExchange rpcExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.rpcExchange = rpcExchange;
        this.objectMapper = new ObjectMapper();
    }

    public ProductMessage sendRpcMessage(ProductMessage message) throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(message);
        String correlationId = UUID.randomUUID().toString();

        MessageProperties props = new MessageProperties();
        props.setCorrelationId(correlationId);
        props.setContentType("application/json");
        props.setReplyTo(AMQ_RABBITMQ_REPLY_TO);

        Message requestMessage = new Message(jsonMessage.getBytes(), props);
        Message responseMessage = rabbitTemplate.sendAndReceive(rpcExchange.getName(), rpcRoutingKey, requestMessage);

        if (responseMessage != null) {
            String responseJson = new String(responseMessage.getBody());
            String responseCorrelationId = responseMessage.getMessageProperties().getCorrelationId();
            log.info("Request corr_id: {}, Response corr_id: {}", correlationId, responseCorrelationId);
            if (correlationId.equals(responseCorrelationId)) {
                return objectMapper.readValue(responseJson, ProductMessage.class);
            } else {
                throw new RuntimeException("Mismatched correlation ID in the response");
            }
        } else {
            throw new RuntimeException("Response timed out or no response received");
        }
    }
}
