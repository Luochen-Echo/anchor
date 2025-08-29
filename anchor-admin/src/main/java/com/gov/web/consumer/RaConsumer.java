package com.gov.web.consumer;

import com.gov.common.constant.DevConstants;
import com.gov.common.constant.DirectRabbitConstant;
import com.gov.core.config.RedisCache;
import com.gov.web.domain.RaData;
import com.gov.web.service.RaDevListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;
/**
 *
 * @author luochen
 */


@Slf4j
@Component
public class RaConsumer {

    private final RedisCache redisCache;
    private final RaDevListService raDevListService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public RaConsumer(RedisCache redisCache,
                                RaDevListService raDevListService,
                                ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.redisCache = redisCache;
        this.raDevListService = raDevListService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @RabbitListener(queues = DirectRabbitConstant.RA_DIRECT_QUEUE)
    public void handleMessage(String msg) {
        try {
            // 1. 基本消息验证
            if (StringUtils.isBlank(msg) || !msg.contains("@")) {
                log.warn("无效的消息格式: {}", msg);
                return;
            }

            // 2. 分离客户端信息和传感器数据
            String[] messageParts = msg.split("@");
            if (messageParts.length != 2) {
                log.warn("消息格式不正确，缺少传感器数据部分: {}", msg);
                return;
            }

            // 3. 处理每个传感器数据条目
            String[] sensorDataEntries = messageParts[1].split(";");
            for (String entry : sensorDataEntries) {
                if (StringUtils.isBlank(entry)) continue;

                processSensorEntry(entry);
            }
        } catch (Exception e) {
            log.error("处理传感器消息时发生异常: {}", msg, e);
        }
    }

    private void processSensorEntry(String entry) {
        try {
            String[] item = entry.split(",");
            if (item.length < 3) {
                log.warn("无效的传感器数据条目: {}", entry);
                return;
            }

            // 跳过无效数据 (-999表示无检测)
            if ("-999".equals(item[2])) {
                return;
            }

            // 解析传感器数据
            RaData raData = parseSensorData(item);
            if (raData == null) {
                return;
            }

            // 存储到Redis
            String redisKey = buildRedisKey(item[0], item[1]);
            redisCache.rightPush(redisKey, raData);

            // 检查是否达到批量大小
            checkBatchSize(redisKey);
        } catch (Exception e) {
            log.error("处理传感器条目失败: {}", entry, e);
        }
    }

    private String buildRedisKey(String deviceId, String channelId) {
        return RA_REDIS_NAME + deviceId + "_" + channelId;
    }

    private void checkBatchSize(String redisKey) {
        int size = redisCache.getCacheList(redisKey).size();
        if (size >= BATCH_SIZE) {
            flushToDatabase(redisKey);
        }
    }

    private RaData parseSensorData(String[] parts) {
        try {
            RaData data = new RaData();
            data.setCreateTime(new Date());

            switch (parts[1]) {
                case "A":  // 温度
                case "C":  // 应变
                case "D":  // 位移
                    data.setValue1(Double.parseDouble(parts[2]));
                    data.setValue2(0.0);
                    break;
                case "B":  // 温湿度
                    if (parts.length < 4) {
                        log.warn("温湿度传感器数据不完整: {}", String.join(",", parts));
                        return null;
                    }
                    data.setValue1(Double.parseDouble(parts[2])); // 湿度
                    data.setValue2(Double.parseDouble(parts[3])); // 温度
                    break;
                default:
                    log.warn("未知的传感器类型: {}", parts[1]);
                    return null;
            }
            return data;
        } catch (NumberFormatException e) {
            log.error("解析传感器数值失败: {}", String.join(",", parts), e);
            return null;
        }
    }



    private void flushToDatabase(String redisKey) {
        List<RaData> dataList = redisCache.getCacheList(redisKey);
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        List<RaData> distinctData = removeConsecutiveDuplicates(dataList);
        try {
            // 解析设备ID和通道ID
            String[] parts = redisKey.replace(RA_REDIS_NAME, "").split("_");
            if (parts.length != 2) {
                log.warn("无效的Redis键格式: {}", redisKey);
                return;
            }

            String tableName = String.format(DevConstants.RA_TABLE_NAME, parts[0], parts[1]);

            threadPoolTaskExecutor.execute(() -> {
                try {
                    raDevListService.insertInfoBatch(tableName, distinctData);
                    log.info("成功批量插入{}条数据到表{}", dataList.size(), tableName);
                } catch (Exception e) {
                    log.error("批量插入数据到表{}失败", tableName, e);
                }finally {
                    redisCache.deleteObject(redisKey);
                }
            });
        } catch (RejectedExecutionException e) {
            log.warn("线程池已满，稍后重试处理键: {}", redisKey);
        } catch (Exception e) {
            log.error("处理Redis键 {} 时发生异常", redisKey, e);
        }
    }

    // 常量定义
    private static final String RA_REDIS_NAME = "ra:";
    private static final int BATCH_SIZE = 120;
    public static List<RaData> removeConsecutiveDuplicates(List<RaData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }

        List<RaData> result = new ArrayList<>();
        RaData previous = dataList.get(0);
        result.add(previous);

        for (int i = 1; i < dataList.size(); i++) {
            RaData current = dataList.get(i);

            // 比较value1和value2是否相同
            boolean valuesEqual = (previous.getValue1() == null ? current.getValue1() == null
                    : previous.getValue1().equals(current.getValue1()))
                    && (previous.getValue2() == null ? current.getValue2() == null
                    : previous.getValue2().equals(current.getValue2()));

            if (!valuesEqual) {
                result.add(current);
                previous = current;
            }
        }

        return result;
    }


}