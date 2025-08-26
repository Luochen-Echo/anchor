package com.gov.ae.netty;


import com.gov.ae.config.ChannelManager;
import com.gov.common.constant.DirectRabbitConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
public class RasterDataHandler extends SimpleChannelInboundHandler<String> {

    private final ChannelManager channelManager;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RasterDataHandler(ChannelManager channelManager,RabbitTemplate rabbitTemplate) {
        this.channelManager = channelManager;
        this.rabbitTemplate = rabbitTemplate;
    }
    private static final ExecutorService RABBIT_SENDER = Executors.newFixedThreadPool(2);
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String clientIp = getClientIp(ctx);
        log.info("Raster client connected: {}", clientIp);
        channelManager.addChannel(clientIp, ctx.channel());
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Raster transmission error: {}", cause.getMessage());
        ctx.close();
    }

    private String getClientIp(ChannelHandlerContext ctx) {
        return ctx.channel().remoteAddress().toString().replace("/", "");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)  {
        try {
            String[] parts = msg.split(",");
            if (parts.length != 10) {
                System.err.println("Invalid data format: " + msg);
                return;
            }
            RABBIT_SENDER.submit(() -> {
                try {
                    rabbitTemplate.convertAndSend(DirectRabbitConstant.AE_DIRECT_EXCHANGE,
                            DirectRabbitConstant.AE_DIRECT_ROUTING, msg);
                } catch (Exception e) {
                    log.error("Failed to send message to RabbitMQ", e);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}