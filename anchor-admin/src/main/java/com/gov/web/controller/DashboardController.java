package com.gov.web.controller;

import com.gov.common.controller.BaseController;
import com.gov.common.util.R;
import com.gov.web.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 可视化大屏控制器
 * @author luochen
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取系统概览数据
     * @return 系统概览统计信息
     */
    @GetMapping("/overview")
    public R getOverview() {
        Map<String, Object> overview = dashboardService.getSystemOverview();
        return R.ok(overview);
    }
}