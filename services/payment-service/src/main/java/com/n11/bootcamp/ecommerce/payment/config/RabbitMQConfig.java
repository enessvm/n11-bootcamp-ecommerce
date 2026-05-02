package com.n11.bootcamp.ecommerce.payment.config;

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

    public static final String QUEUE_SAGA_COMMANDS = "payment-service.commands";

    public static final String DLX_NAME            = "payment-service.dlx";
    public static final String DLQ_SAGA_COMMANDS   = "payment-service.commands.dlq";

    // ---- Inbound: payment.commands ----

    @Bean
    public TopicExchange paymentCommandsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_PAYMENT_COMMANDS, true, false);
    }

    @Bean
    public Queue sagaCommandsQueue() {
        return QueueBuilder.durable(QUEUE_SAGA_COMMANDS)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_SAGA_COMMANDS)
                .build();
    }

    @Bean
    public Binding chargeCommandBinding(Queue sagaCommandsQueue,
                                        TopicExchange paymentCommandsExchange) {
        return BindingBuilder.bind(sagaCommandsQueue)
                .to(paymentCommandsExchange)
                .with(RoutingKeys.PAYMENT_CHARGE);
    }

    // ---- Outbound: payment.events ----

    @Bean
    public TopicExchange paymentEventsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_PAYMENT_EVENTS, true, false);
    }

    // ---- DLX + DLQ ----

    @Bean
    public TopicExchange paymentServiceDlx() {
        return new TopicExchange(DLX_NAME, true, false);
    }

    @Bean
    public Queue sagaCommandsDlq() {
        return QueueBuilder.durable(DLQ_SAGA_COMMANDS).build();
    }

    @Bean
    public Binding sagaCommandsDlqBinding(Queue sagaCommandsDlq,
                                          TopicExchange paymentServiceDlx) {
        return BindingBuilder.bind(sagaCommandsDlq)
                .to(paymentServiceDlx)
                .with(QUEUE_SAGA_COMMANDS);
    }

    // ---- JSON conversion ----

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