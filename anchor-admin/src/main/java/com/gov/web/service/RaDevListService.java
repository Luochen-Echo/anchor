package com.gov.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gov.web.domain.AeData;
import com.gov.web.domain.RaData;
import com.gov.web.domain.RaDevList;

import java.util.List;

public interface RaDevListService extends IService<RaDevList> {
    RaDevList checkDeviceExist(String deviceId);
    void createDeviceIfNotExist(String deviceId,String type);
    void insertInfoBatch(String tableName, List<RaData> list);
    List<RaData> getDevHistoryTable(String devSn,String strTime, String endTime);
    void batchRemoveDuplicates();
    void  resetAutoIncrement();
}