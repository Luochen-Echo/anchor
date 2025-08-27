package com.gov.web.service;

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
}