package com.kingshuk.messaging.publisherconfirms;

import com.kingshuk.messaging.util.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class PublisherConfirmBatchSender {

    public static void main(String[] args) throws IOException, TimeoutException {
        Queue<String> messages = RabbitMQUtils.getMessages();
        ConnectionFactory connectionFactory = RabbitMQUtils.getConnectionFactory();
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel();) {
            channel.confirmSelect();
            final String queue = channel.queueDeclare().getQueue();

            int batchSize = 2;
            int messageSentCount = 0;

            while (hasMoreMessagesToPublish(messages)) {
                sendMessage(channel, queue, messages.poll());
                messageSentCount++;

                if (messageSentCount == batchSize) {
                    channel.waitForConfirms(5000);
                    System.out.println("Have received confirmation from the broker");
                    messageSentCount = 0;
                }

            }

            if (messageSentCount > 0) {
                channel.waitForConfirms(5000);
                System.out.println("Have received confirmation from the broker");
            }
        } catch (TimeoutException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasMoreMessagesToPublish(Queue<String> messages) {
        return !messages.isEmpty();
    }

    private static void sendMessage(Channel channel,
                                    String queue,
                                    String message) throws IOException {

        channel.basicPublish("", queue, null,
                message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
    }
}