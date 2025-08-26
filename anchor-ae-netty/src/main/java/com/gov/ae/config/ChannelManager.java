package com.gov.ae.config;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelManager {
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();


    public void addChannel(String clientId, Channel channel) {
        channelMap.put(clientId, channel);
    }

    public void removeChannel(String clientId) {
        channelMap.remove(clientId);
    }

    public Channel getChannel(String clientId) {
        return channelMap.get(clientId);
    }


}
