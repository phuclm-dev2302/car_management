package org.example.car_management_system.rabbitmq;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = "notification-queue")
    public void listerNotificationQueue(String message){
        System.out.println("Nghe thông báo từ 'notification-queue': " + message);
    }

}
