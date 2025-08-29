package com.gov.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gov.common.constant.DevConstants;
import com.gov.common.util.RaEnum;
import com.gov.core.exception.BusinessException;
import com.gov.web.domain.RaData;
import com.gov.web.domain.RaDevList;
import com.gov.web.mapper.RaDevListMapper;
import com.gov.web.service.RaDevListService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class RaDevListServiceImpl extends ServiceImpl<RaDevListMapper, RaDevList> implements RaDevListService {


    @Override
    @Cacheable(value = "AeDeviceCache", key = "#deviceId")
    public RaDevList checkDeviceExist(String deviceId) {
        return super.lambdaQuery().eq(RaDevList::getDeviceCode, deviceId).one();
    }

    @Override
    @CacheEvict(value = "AeDeviceCache", key = "#deviceId")
    @Transactional
    public void createDeviceIfNotExist(String deviceId,String type) {
        if (checkDeviceExist(deviceId)==null) {
            save(RaDevList.builder().deviceCode(deviceId).type(RaEnum.getCode(type)).build());
            String tableName=String.format(DevConstants.RA_TABLE_NAME, deviceId, type);
            baseMapper.createTable(tableName);
        }
    }

    @Override
    @Transactional
    public void insertInfoBatch(String tableName, List<RaData> list) {
        baseMapper.insertInfoBatch(tableName,list);
    }

    @Override
    public List<RaData> getDevHistoryTable(String devSn, String strTime, String endTime) {
        RaDevList r=checkDeviceExist(devSn);
        if(r==null){
            throw new BusinessException("设备不存在");
        }

        String tableName=String.format(DevConstants.RA_TABLE_NAME, devSn,RaEnum.fromValue(r.getType()));
        List<RaData> devHistories = baseMapper.queryTable( tableName,  strTime,  endTime);
        
        // 设置 deviceId 值
        devHistories.forEach(data -> data.setDeviceId(devSn));
        
        return devHistories;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 不使用事务，避免长事务问题
    public void batchRemoveDuplicates() {
        List<RaDevList> devices = super.baseMapper.selectList(new QueryWrapper<>());

        devices.forEach(device -> {
            String tableName = String.format(DevConstants.RA_TABLE_NAME,
                    device.getDeviceCode(),
                    RaEnum.fromValue(device.getType()));

            processSingleTable(tableName);
        });
    }
    /**
     * 处理单个表的数据去重
     */
    private void processSingleTable(String tableName) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理表: {}", tableName);

        int totalProcessed = 0;
        int totalDeleted = 0;
        List<Long> idsToDelete = new ArrayList<>(DELETE_BATCH_SIZE);
        RaData lastRecord = null;
        int offset = 0;

        try {
            while (true) {
                // 使用独立短事务处理每个批次
                Map<String, Object> batchResult = processBatch(tableName, offset, lastRecord, idsToDelete);

                int processed = (int) batchResult.get("processed");
                lastRecord = (RaData) batchResult.get("lastRecord");
                int deleted = (int) batchResult.get("deleted");

                if (processed == 0) break;

                totalProcessed += processed;
                totalDeleted += deleted;
                offset += BATCH_SIZE;

                // 定期输出进度
                if (offset % (BATCH_SIZE * 10) == 0) {
                    log.info("表 {} 处理进度 - 已处理: {} 条, 已删除: {} 条",
                            tableName, totalProcessed, totalDeleted);
                }
            }

            // 处理剩余未删除的记录
            if (!idsToDelete.isEmpty()) {
                executeBatchDelete(tableName, idsToDelete);
            }

            // 重置自增ID（单独操作）
           // resetAutoIncrement(tableName);

        } catch (Exception e) {
            log.error("处理表 {} 时发生错误: {}", tableName, e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("表 {} 处理完成 - 总处理: {} 条, 总删除: {} 条, 耗时: {} 秒",
                    tableName, totalProcessed, totalDeleted, (endTime - startTime) / 1000);
        }
    }
    // 每批次处理大小
    private static final int BATCH_SIZE = 5000;
    // 批量删除大小
    private static final int DELETE_BATCH_SIZE = 500;

    /**
     * 处理单个批次（使用独立短事务）
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public Map<String, Object> processBatch(String tableName, int offset, RaData lastRecord, List<Long> idsToDelete) {
        Map<String, Object> result = new HashMap<>();
        List<RaData> currentBatch = baseMapper.selectPageOrderByCreateTime(tableName, offset, BATCH_SIZE);
        int batchDeleted = 0;

        for (RaData current : currentBatch) {
            if (lastRecord != null && isDuplicate(lastRecord, current)) {
                idsToDelete.add(current.getId());
                batchDeleted++;

                if (idsToDelete.size() >= DELETE_BATCH_SIZE) {
                    executeBatchDelete(tableName, idsToDelete);
                }
            } else {
                lastRecord = current;
            }
        }

        result.put("processed", currentBatch.size());
        result.put("lastRecord", lastRecord);
        result.put("deleted", batchDeleted);
        return result;
    }

    /**
     * 执行批量删除
     */
    private void executeBatchDelete(String tableName, List<Long> idsToDelete) {
        if (!idsToDelete.isEmpty()) {
            baseMapper.deleteBatchIds(tableName, idsToDelete);
            idsToDelete.clear();
        }
    }

    /**
     * 重置自增ID（独立操作）
     */
    @Transactional()
    @Override
    public void resetAutoIncrement() {

        try {
            List<RaDevList> devices = super.baseMapper.selectList(new QueryWrapper<>());

            devices.forEach(device -> {
                String tableName = String.format(DevConstants.RA_TABLE_NAME,
                        device.getDeviceCode(),
                        RaEnum.fromValue(device.getType()));
                // 使用 %s 作为占位符，并正确转义反引号
                System.out.println(String.format("ALTER TABLE `%s` MODIFY id INT(11) NOT NULL FIRST,DROP PRIMARY KEY", tableName)+";");
                System.out.println(String.format("ALTER TABLE `%s` ADD id2 INT(11) NOT NULL AUTO_INCREMENT FIRST,ADD PRIMARY KEY (id2)", tableName)+";");
                System.out.println(String.format("ALTER TABLE `%s` DROP id", tableName)+";");
                System.out.println(String.format("ALTER TABLE `%s` CHANGE id2 id INT(11) NOT NULL AUTO_INCREMENT FIRST", tableName)+";");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断两条记录是否重复
     */
    private boolean isDuplicate(RaData a, RaData b) {
        return Objects.equals(a.getValue1(), b.getValue1())
                && Objects.equals(a.getValue2(), b.getValue2());
    }




}