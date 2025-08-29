package com.gov.web.consumer;


import com.gov.common.constant.DevConstants;
import com.gov.common.constant.DirectRabbitConstant;
import com.gov.common.util.ParseUtils;
import com.gov.core.config.RedisCache;
import com.gov.web.domain.AeData;
import com.gov.web.service.AeDevListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 * @author luochen
 */

@Component
@Slf4j
public class AeConsumer {



    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private RedisCache redisCache;
    @Resource
    private AeDevListService aeDevListService;
    /**
     * 处理接收到的消息
     *
     * @param msg
     */
    @RabbitListener(queues = DirectRabbitConstant.AE_DIRECT_QUEUE)
    public void handleMessage(String msg) {
        try {
            String[] parts = msg.split(",");
            String deviceId = parts[0].replace("\"", "");
            aeDevListService.createDeviceIfNotExist(deviceId);

            double amp = Double.parseDouble(parts[1]);
            double asl = Double.parseDouble(parts[2]);
            double energy = Double.parseDouble(parts[3]);
            double rms = Double.parseDouble(parts[4]);
            int riseTime = Integer.parseInt(parts[5]);
            int riseCount = Integer.parseInt(parts[6]);
            int ringCount = Integer.parseInt(parts[7]);
            int duration = Integer.parseInt(parts[8]);
            double timestamp = ParseUtils.parseSafeDouble(parts[9]);
            AeData ae=new AeData(null,deviceId,amp,asl,energy,rms,riseTime,riseCount,ringCount,duration,timestamp,new Date());
            redisCache.rightPush(DevConstants.DEV_AE_ADD + deviceId, ae);
            List<AeData> list = redisCache.getCacheList(DevConstants.DEV_AE_ADD + deviceId);
            if(list.size()>=100){
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        String tableName=String.format(DevConstants.AE_TABLE_NAME, deviceId);
                        aeDevListService.insertInfoBatch(tableName, list);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        redisCache.deleteObject(DevConstants.DEV_AE_ADD + deviceId);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
