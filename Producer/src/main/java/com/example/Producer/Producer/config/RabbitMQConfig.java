package com.example.Producer.Producer.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@Slf4j
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue}")
    private String queue;

    @Value("${rabbitmq.routing.key}")
    private String key;

    @Value("${rabbitmq.rpc.exchange}")
    private String rpcExchange;

    @Value("${rabbitmq.rpc.queue}")
    private String rpcQueue;

    @Value("${rabbitmq.rpc.routing.key}")
    private String rpcKey;



    @Bean
    public Queue asyncQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange asyncExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding asyncBinding(Queue asyncQueue, DirectExchange asyncExchange) {
        return BindingBuilder.bind(asyncQueue).to(asyncExchange).with(key);
    }

    @Bean
    public Queue rpcQueue() {
        return new Queue(rpcQueue, true);
    }

    @Bean
    public DirectExchange rpcExchange() {
        return new DirectExchange(rpcExchange);
    }

    @Bean
    public Binding rpcBinding(Queue rpcQueue, DirectExchange rpcExchange) {
        return BindingBuilder.bind(rpcQueue).to(rpcExchange).with(rpcKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReplyTimeout(10000);
        return template;
    }
}
