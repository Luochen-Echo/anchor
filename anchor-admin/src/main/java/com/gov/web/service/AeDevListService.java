package com.gov.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gov.web.domain.AeData;
import com.gov.web.domain.AeDevList;

import java.util.List;

/**
 *
 * @author luochen
 */

public interface AeDevListService extends IService<AeDevList> {
    boolean checkDeviceExist(String deviceId);
    void createDeviceIfNotExist(String deviceId);
    void insertInfoBatch(String tableName, List<AeData> list);
    List<AeData> getDevHistoryTable(String devSn,String strTime, String endTime);
}
