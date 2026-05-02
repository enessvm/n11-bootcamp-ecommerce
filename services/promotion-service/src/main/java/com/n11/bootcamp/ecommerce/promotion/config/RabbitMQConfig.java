package com.n11.bootcamp.ecommerce.promotion.config;

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

    public static final String QUEUE_APPLY_COMMANDS    = "promotion-service.apply-commands";
    public static final String QUEUE_REVERT_COMMANDS   = "promotion-service.revert-commands";
    public static final String DLX_NAME                = "promotion-service.dlx";
    public static final String DLQ_APPLY_COMMANDS      = "promotion-service.apply-commands.dlq";
    public static final String DLQ_REVERT_COMMANDS     = "promotion-service.revert-commands.dlq";

    // ---- Inbound: promotion.commands ----

    @Bean
    public TopicExchange promotionCommandsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_PROMOTION_COMMANDS, true, false);
    }

    @Bean
    public Queue applyCommandsQueue() {
        return QueueBuilder.durable(QUEUE_APPLY_COMMANDS)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_APPLY_COMMANDS)
                .build();
    }

    @Bean
    public Binding applyCommandsBinding(Queue applyCommandsQueue,
                                        TopicExchange promotionCommandsExchange) {
        return BindingBuilder.bind(applyCommandsQueue)
                .to(promotionCommandsExchange)
                .with(RoutingKeys.PROMOTION_APPLY);
    }

    @Bean
    public Queue revertCommandsQueue() {
        return QueueBuilder.durable(QUEUE_REVERT_COMMANDS)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_REVERT_COMMANDS)
                .build();
    }

    @Bean
    public Binding revertCommandsBinding(Queue revertCommandsQueue,
                                         TopicExchange promotionCommandsExchange) {
        return BindingBuilder.bind(revertCommandsQueue)
                .to(promotionCommandsExchange)
                .with(RoutingKeys.PROMOTION_REVERT);
    }

    // ---- Outbound: promotion.events ----

    @Bean
    public TopicExchange promotionEventsExchange() {
        return new TopicExchange(RoutingKeys.EXCHANGE_PROMOTION_EVENTS, true, false);
    }

    // ---- DLX + DLQ ----

    @Bean
    public TopicExchange promotionServiceDlx() {
        return new TopicExchange(DLX_NAME, true, false);
    }

    @Bean
    public Queue applyCommandsDlq() {
        return QueueBuilder.durable(DLQ_APPLY_COMMANDS).build();
    }

    @Bean
    public Binding applyCommandsDlqBinding(Queue applyCommandsDlq,
                                           TopicExchange promotionServiceDlx) {
        return BindingBuilder.bind(applyCommandsDlq)
                .to(promotionServiceDlx)
                .with(QUEUE_APPLY_COMMANDS);
    }

    @Bean
    public Queue revertCommandsDlq() {
        return QueueBuilder.durable(DLQ_REVERT_COMMANDS).build();
    }

    @Bean
    public Binding revertCommandsDlqBinding(Queue revertCommandsDlq,
                                            TopicExchange promotionServiceDlx) {
        return BindingBuilder.bind(revertCommandsDlq)
                .to(promotionServiceDlx)
                .with(QUEUE_REVERT_COMMANDS);
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