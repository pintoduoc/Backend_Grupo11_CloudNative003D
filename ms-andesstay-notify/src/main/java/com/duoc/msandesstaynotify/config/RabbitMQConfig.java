package com.duoc.msandesstaynotify.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${app.rabbitmq.exchanges.direct}")
    private String directExchangeName;

    @Value("${app.rabbitmq.exchanges.topic}")
    private String topicExchangeName;

    @Value("${app.rabbitmq.exchanges.dlx}")
    private String dlxExchangeName;

    @Value("${app.rabbitmq.queues.email}")
    private String emailQueueName;

    @Value("${app.rabbitmq.queues.email-dlq}")
    private String emailDlqName;

    @Value("${app.rabbitmq.queues.housekeeping}")
    private String housekeepingQueueName;

    @Value("${app.rabbitmq.queues.housekeeping-dlq}")
    private String housekeepingDlqName;

    @Value("${app.rabbitmq.queues.voucher}")
    private String voucherQueueName;

    @Value("${app.rabbitmq.queues.voucher-dlq}")
    private String voucherDlqName;

    @Value("${app.rabbitmq.routing.email-direct}")
    private String emailDirectKey;

    @Value("${app.rabbitmq.routing.email-topic}")
    private String emailTopicKey;

    @Value("${app.rabbitmq.routing.email-dlq}")
    private String emailDlqKey;

    @Value("${app.rabbitmq.routing.housekeeping-direct}")
    private String housekeepingDirectKey;

    @Value("${app.rabbitmq.routing.housekeeping-topic}")
    private String housekeepingTopicKey;

    @Value("${app.rabbitmq.routing.housekeeping-dlq}")
    private String housekeepingDlqKey;

    @Value("${app.rabbitmq.routing.voucher-direct}")
    private String voucherDirectKey;

    @Value("${app.rabbitmq.routing.voucher-topic}")
    private String voucherTopicKey;

    @Value("${app.rabbitmq.routing.voucher-dlq}")
    private String voucherDlqKey;

    //                              1. EXCHANGES

    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(directExchangeName, true, false);
    }

    @Bean
    public TopicExchange cmdTopicExchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxExchangeName, true, false);
    }

    //                              2. COLAS PRINCIPALES (CON DLX)

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", emailDlqKey)
                .build();
    }

    @Bean
    public Queue housekeepingQueue() {
        return QueueBuilder.durable(housekeepingQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", housekeepingDlqKey)
                .build();
    }

    @Bean
    public Queue voucherQueue() {
        return QueueBuilder.durable(voucherQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", voucherDlqKey)
                .build();
    }

    //                              3. DEAD LETTER QUEUES (DLQ)

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(emailDlqName).build();
    }

    @Bean
    public Queue housekeepingDlq() {
        return QueueBuilder.durable(housekeepingDlqName).build();
    }

    @Bean
    public Queue voucherDlq() {
        return QueueBuilder.durable(voucherDlqName).build();
    }

    //                              4. BINDINGS DIRECT EXCHANGE

    @Bean
    public Binding emailDirectBinding() {
        return BindingBuilder.bind(emailQueue()).to(cmdDirectExchange()).with(emailDirectKey);
    }

    @Bean
    public Binding housekeepingDirectBinding() {
        return BindingBuilder.bind(housekeepingQueue()).to(cmdDirectExchange()).with(housekeepingDirectKey);
    }

    @Bean
    public Binding voucherDirectBinding() {
        return BindingBuilder.bind(voucherQueue()).to(cmdDirectExchange()).with(voucherDirectKey);
    }

    //                              5. BINDINGS TOPIC EXCHANGE

    @Bean
    public Binding emailTopicBinding() {
        return BindingBuilder.bind(emailQueue()).to(cmdTopicExchange()).with(emailTopicKey);
    }

    @Bean
    public Binding housekeepingTopicBinding() {
        return BindingBuilder.bind(housekeepingQueue()).to(cmdTopicExchange()).with(housekeepingTopicKey);
    }

    @Bean
    public Binding voucherTopicBinding() {
        return BindingBuilder.bind(voucherQueue()).to(cmdTopicExchange()).with(voucherTopicKey);
    }

    //                              6. BINDINGS DEAD LETTER EXCHANGE (DLX -> DLQ)

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(deadLetterExchange()).with(emailDlqKey);
    }

    @Bean
    public Binding housekeepingDlqBinding() {
        return BindingBuilder.bind(housekeepingDlq()).to(deadLetterExchange()).with(housekeepingDlqKey);
    }

    @Bean
    public Binding voucherDlqBinding() {
        return BindingBuilder.bind(voucherDlq()).to(deadLetterExchange()).with(voucherDlqKey);
    }

    //                              7. SERIALIZADOR JSON Y RABBIT ADMIN

    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    //                              7. SERIALIZADOR JSON Y RABBIT ADMIN
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }
}