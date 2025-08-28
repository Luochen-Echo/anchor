package com.gov.web.service;

import com.gov.web.domain.AeData;

import java.util.List;
import java.util.Map;

/**
 * 可视化大屏数据服务接口
 * @author luochen
 */
public interface DashboardService {
    
    /**
     * 获取系统概览数据
     * @return 系统概览统计信息
     */
    Map<String, Object> getSystemOverview();
    
    /**
     * 获取AE实时数据
     * @return 各AE设备最新的声发射参数
     */
    List<AeData> getAeLatestData();
}