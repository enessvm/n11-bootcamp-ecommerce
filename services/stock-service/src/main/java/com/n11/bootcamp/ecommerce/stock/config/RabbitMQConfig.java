package com.n11.bootcamp.ecommerce.stock.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {


    public static final String QUEUE_PRODUCT_CREATED     = "stock-service.product.created";
    public static final String DLX_NAME                  = "stock-service.dlx";
    public static final String DLQ_PRODUCT_CREATED       = "stock-service.product.created.dlq";

    // ---- Source exchange (declared by product-service, referenced here) ----
    @Bean
    public TopicExchange productEventsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_PRODUCT_EVENTS, true, false);
    }



    @Bean
    public Queue productCreatedQueue() {
        return QueueBuilder.durable(QUEUE_PRODUCT_CREATED)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_PRODUCT_CREATED)
                .build();
    }

    @Bean
    public Binding productCreatedBinding(Queue productCreatedQueue,
                                         TopicExchange productEventsExchange) {
        return BindingBuilder.bind(productCreatedQueue)
                .to(productEventsExchange)
                .with(RoutingKeys.PRODUCT_CREATED);
    }

    // ---- Dead-letter exchange + queue ----

    @Bean
    public TopicExchange stockServiceDlx() {
        return new TopicExchange(DLX_NAME, true, false);
    }

    @Bean
    public Queue productCreatedDlq() {
        return QueueBuilder.durable(DLQ_PRODUCT_CREATED).build();
    }

    @Bean
    public Binding productCreatedDlqBinding(Queue productCreatedDlq,
                                            TopicExchange stockServiceDlx) {
        return BindingBuilder.bind(productCreatedDlq)
                .to(stockServiceDlx)
                .with(QUEUE_PRODUCT_CREATED);
    }

    // ---- JSON message conversion ----
    @Bean
    @Primary
    public MessageConverter jacksonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }
}