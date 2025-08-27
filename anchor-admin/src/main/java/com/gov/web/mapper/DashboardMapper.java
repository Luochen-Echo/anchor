package com.gov.web.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 可视化大屏数据查询Mapper
 * @author luochen
 */
@Mapper
public interface DashboardMapper {
    
    /**
     * 获取AE设备数量
     * @return AE设备数量
     */
    Integer getAeDeviceCount();
    
    /**
     * 获取RA设备数量  
     * @return RA设备数量
     */
    Integer getRaDeviceCount();
    
    /**
     * 获取今日AE数据总量
     * @param today 今日日期
     * @return 今日AE数据总量
     */
    Long getTodayAeDataCount(String today);
    
    /**
     * 获取今日RA数据总量
     * @param today 今日日期
     * @return 今日RA数据总量
     */
    Long getTodayRaDataCount(String today);
    
    /**
     * 获取历史AE数据总量
     * @return 历史AE数据总量
     */
    Long getTotalAeDataCount();
    
    /**
     * 获取历史RA数据总量
     * @return 历史RA数据总量
     */
    Long getTotalRaDataCount();
}