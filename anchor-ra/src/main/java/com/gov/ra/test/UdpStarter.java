package com.gov.ra.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/*
Springboot 服务启动时 同时启动 udp server 监听端口application.yml 的 udp.bport
 */
@Component
public class UdpStarter implements CommandLineRunner {

    @Autowired
    private NettyUdpServer nettyUdpServer;

    @Override
    public void run(String... args) throws Exception {
        nettyUdpServer.init(8001);
    }
}
