package com.gov.web.service.impl;

import com.gov.web.domain.AeData;
import com.gov.web.mapper.DashboardMapper;
import com.gov.web.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可视化大屏数据服务实现类
 * @author luochen
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 获取设备统计
        Integer aeDeviceCount = dashboardMapper.getAeDeviceCount();
        Integer raDeviceCount = dashboardMapper.getRaDeviceCount();
        
        // 获取今日数据统计
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Long todayAeDataCount = dashboardMapper.getTodayAeDataCount(today);
        Long todayRaDataCount = dashboardMapper.getTodayRaDataCount(today);
        
        // 获取历史数据总量
        Long totalAeDataCount = dashboardMapper.getTotalAeDataCount();
        Long totalRaDataCount = dashboardMapper.getTotalRaDataCount();
        
        // 组装返回数据
        overview.put("aeDeviceCount", aeDeviceCount != null ? aeDeviceCount : 0);
        overview.put("raDeviceCount", raDeviceCount != null ? raDeviceCount : 0);
        overview.put("totalDeviceCount", (aeDeviceCount != null ? aeDeviceCount : 0) + (raDeviceCount != null ? raDeviceCount : 0));
        
        overview.put("todayAeDataCount", todayAeDataCount != null ? todayAeDataCount : 0L);
        overview.put("todayRaDataCount", todayRaDataCount != null ? todayRaDataCount : 0L);
        overview.put("todayTotalDataCount", (todayAeDataCount != null ? todayAeDataCount : 0L) + (todayRaDataCount != null ? todayRaDataCount : 0L));
        
        overview.put("totalAeDataCount", totalAeDataCount != null ? totalAeDataCount : 0L);
        overview.put("totalRaDataCount", totalRaDataCount != null ? totalRaDataCount : 0L);
        overview.put("totalDataCount", (totalAeDataCount != null ? totalAeDataCount : 0L) + (totalRaDataCount != null ? totalRaDataCount : 0L));
        
        overview.put("lastUpdateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return overview;
    }

    @Override
    public List<AeData> getAeLatestData() {
        return dashboardMapper.getAeLatestData();
    }
}