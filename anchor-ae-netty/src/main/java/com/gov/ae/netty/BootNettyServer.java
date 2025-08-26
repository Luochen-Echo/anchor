package com.gov.ae.netty;


import com.gov.ae.config.ChannelManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class BootNettyServer implements ApplicationRunner {


    @Autowired
    private ChannelManager channelManager;

    @Value("${tcp.port}")
    private Integer tcpPort;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 	启动服务
     */
    public void startup(int port) {

        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("linux")) {
                bossGroup = new EpollEventLoopGroup(2);
                workGroup = new EpollEventLoopGroup(4);
            } else {
                bossGroup = new NioEventLoopGroup(2);
                workGroup = new NioEventLoopGroup(4);
            }

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            Class<? extends ServerChannel> channelClass;
            if (osName.contains("linux")) {
                channelClass = EpollServerSocketChannel.class;
            } else {
                channelClass = NioServerSocketChannel.class;
            }
            bootstrap.channel(channelClass);

            bootstrap.option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_RCVBUF, 10485760);

            bootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 使用 Netty 的 ByteBuf 对象池
            PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
            bootstrap.option(ChannelOption.ALLOCATOR, allocator)
                    .childOption(ChannelOption.ALLOCATOR, allocator);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline channelPipeline = ch.pipeline();
                    // Tcp 心跳设置
                    channelPipeline.addLast(new StringDecoder(CharsetUtil.US_ASCII));
                    channelPipeline.addLast(new StringEncoder(CharsetUtil.US_ASCII));
                    channelPipeline.addLast(new IdleStateHandler(180,1000, 1200, TimeUnit.SECONDS));
                    channelPipeline.addLast(new TdskDataDecoder());
                    channelPipeline.addLast(new RasterDataHandler(channelManager,rabbitTemplate));

                }
            });
            ChannelFuture f = bootstrap.bind(port).sync();
            if(f.isSuccess()){
                f.channel().closeFuture().sync();
            } else {
                log.info("启动netty监听端口{}",tcpPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.startup(tcpPort);
    }
    /**
     * 	关闭服务
     */
    public void shutdown() throws InterruptedException {
        if (workGroup != null && bossGroup != null) {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }
}