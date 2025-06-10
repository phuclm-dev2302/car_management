package org.example.car_management_system.rabbitmq;

import org.example.car_management_system.common_event.OtpEmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessageMail(OtpEmailMessage message){
        rabbitTemplate.convertAndSend("send-mail", "send-mail-topic", message);
    }
}