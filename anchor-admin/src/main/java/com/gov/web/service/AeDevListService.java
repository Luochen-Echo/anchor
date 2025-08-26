package com.gov.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gov.web.domain.AeData;
import com.gov.web.domain.AeDevList;

import java.util.List;

/**
* @author 28440
* @description 针对表【ae_dev_list】的数据库操作Service
* @createDate 2025-04-07 17:06:31
*/
public interface AeDevListService extends IService<AeDevList> {
    boolean checkDeviceExist(String deviceId);
    void createDeviceIfNotExist(String deviceId);
    void insertInfoBatch(String tableName, List<AeData> list);
    List<AeData> getDevHistoryTable(String devSn,String strTime, String endTime);
}
