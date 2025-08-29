package com.gov.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gov.common.constant.DevConstants;
import com.gov.core.exception.BusinessException;
import com.gov.web.domain.AeData;
import com.gov.web.domain.AeDevList;
import com.gov.web.mapper.AeDevListMapper;
import com.gov.web.service.AeDevListService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * @author luochen
 */

@Service
@Transactional(readOnly = true)
public class AeDevListServiceImpl extends ServiceImpl<AeDevListMapper, AeDevList>
    implements AeDevListService {



    @Override
    @Cacheable(value = "deviceCache", key = "#deviceId")
    public boolean checkDeviceExist(String deviceId) {
        return super.lambdaQuery().eq(AeDevList::getDeviceId, deviceId).exists();
    }

    @Override
    @CacheEvict(value = "deviceCache", key = "#deviceId")
    @Transactional
    public void createDeviceIfNotExist(String deviceId) {
        if (!checkDeviceExist(deviceId)) {
            save(AeDevList.builder().deviceId(deviceId).build());
            String tableName=String.format(DevConstants.AE_TABLE_NAME, deviceId);
            baseMapper.createTable(tableName);
        }
    }

    @Override
    @Transactional
    public void insertInfoBatch(String tableName, List<AeData> list) {
        baseMapper.insertInfoBatch(tableName,list);
    }

    @Override
    public List<AeData> getDevHistoryTable(String devSn, String strTime, String endTime) {
        if(!checkDeviceExist(devSn)){
            throw new BusinessException("设备不存在");
        }
        String tableName=String.format(DevConstants.AE_TABLE_NAME, devSn);
        List<AeData> devHistories = baseMapper.queryTable( tableName,  strTime,  endTime);
        return devHistories;
    }
}




