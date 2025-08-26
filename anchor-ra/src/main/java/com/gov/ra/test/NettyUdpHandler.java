package com.gov.ra.test;

import com.gov.ra.config.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;

import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


@Slf4j
@Component
public class NettyUdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NettyUdpHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // 修复后的正则表达式
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)@" +
                    "(\\d+-\\d+-\\d+,[A-D],-?(\\d{1,3}|999)(\\.\\d+)?(;\\d+-\\d+-\\d+,[A-D],-?(\\d{1,3}|999)(\\.\\d+)?)*;?)$");
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        try {

            ByteBuf byteBuf = packet.content();
            String rawMessage = byteBuf.toString(CharsetUtil.UTF_8);
            if (!isValidMessage(rawMessage)) {
                log.warn("Invalid message format: {}", rawMessage);
                return;
            }
            rabbitTemplate.convertAndSend("raDirectExchange",
                    "raDirectRouting", rawMessage);
        } catch (Exception e) {
            log.info("netty udp "+e.toString());
        }
    }

    private boolean isValidMessage(String message) {
        return MESSAGE_PATTERN.matcher(message).matches();
    }

}
