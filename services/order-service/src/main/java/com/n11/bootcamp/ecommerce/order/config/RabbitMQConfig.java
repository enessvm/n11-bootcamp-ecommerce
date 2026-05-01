package com.n11.bootcamp.ecommerce.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.n11.bootcamp.ecommerce.events.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    // ---- Queue + exchange + DLQ names ----

    public static final String QUEUE_STOCK_REPLIES     = "order-service.stock.events";
    public static final String DLX_NAME                = "order-service.dlx";
    public static final String DLQ_STOCK_REPLIES       = "order-service.stock.events.dlq";

    // ---- Outbound ----

    @Bean
    public TopicExchange stockCommandsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_STOCK_COMMANDS, true, false);
    }

    // ---- Inbound ----

    @Bean
    public TopicExchange stockEventsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_STOCK_EVENTS, true, false);
    }

    @Bean
    public Queue stockRepliesQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_REPLIES)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_STOCK_REPLIES)
                .build();
    }

    @Bean
    public Binding stockReservedBinding(Queue stockRepliesQueue,
                                        TopicExchange stockEventsExchange) {
        return BindingBuilder.bind(stockRepliesQueue)
                .to(stockEventsExchange)
                .with(RoutingKeys.STOCK_RESERVED);
    }

    @Bean
    public Binding stockReservationFailedBinding(Queue stockRepliesQueue,
                                                 TopicExchange stockEventsExchange) {
        return BindingBuilder.bind(stockRepliesQueue)
                .to(stockEventsExchange)
                .with(RoutingKeys.STOCK_RESERVATION_FAILED);
    }

    // ---- DLX + DLQ ----

    @Bean
    public TopicExchange orderServiceDlx() {
        return new TopicExchange(DLX_NAME, true, false);
    }

    @Bean
    public Queue stockRepliesDlq() {
        return QueueBuilder.durable(DLQ_STOCK_REPLIES).build();
    }

    @Bean
    public Binding stockRepliesDlqBinding(Queue stockRepliesDlq,
                                          TopicExchange orderServiceDlx) {
        return BindingBuilder.bind(stockRepliesDlq)
                .to(orderServiceDlx)
                .with(QUEUE_STOCK_REPLIES);
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

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         @Qualifier("jacksonMessageConverter") MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setMandatory(true);
        return template;
    }
}