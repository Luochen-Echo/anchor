package com.gov.ae.netty;

import com.gov.common.constant.DirectRabbitConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitMQTest {

    private static final String HOST = "171.43.138.192"; // 替换为你的 RabbitMQ 地址
    private static final int PORT = 5672;               // AMQP 默认端口
    private static final String USERNAME = "admin";      // 用户名
    private static final String PASSWORD = "huangguan008";// 密码
    private static final String VIRTUAL_HOST = "/";      // 虚拟主机

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUAL_HOST);

        try {
            // 建立连接
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();



            // 发送一条测试消息
            String message = "Hello from Java Main Method!";
            channel.basicPublish(DirectRabbitConstant.AE_DIRECT_EXCHANGE, DirectRabbitConstant.AE_DIRECT_ROUTING, null, message.getBytes());

            // 接收消息回调
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String receivedMessage = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + receivedMessage + "'");
            };

            // 消费消息
            channel.basicConsume(DirectRabbitConstant.AE_DIRECT_QUEUE, true, deliverCallback, consumerTag -> {});

            // 防止主线程退出
            Thread.sleep(5000);

            // 关闭资源
            channel.close();
            connection.close();

        } catch (Exception e) {
            System.err.println("RabbitMQ Test Failed: ");
            e.printStackTrace();
        }
    }
}
