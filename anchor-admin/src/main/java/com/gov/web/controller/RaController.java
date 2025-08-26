package com.gov.web.controller;

import com.gov.common.constant.HttpStatus;
import com.gov.common.controller.BaseController;
import com.gov.common.page.TableDataInfo;
import com.gov.common.util.R;
import com.gov.web.domain.AeData;
import com.gov.web.domain.RaData;
import com.gov.web.service.AeDevListService;
import com.gov.web.service.RaDevListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ra")
public class RaController extends BaseController {

    @Autowired
    private RaDevListService raDevListService;

    @GetMapping(value = "/getDevHistory")
    public TableDataInfo getDevHistoryTable(String devSn,
                                            String strTime, String endTime) {
        if (StringUtils.isBlank(devSn)) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("请传入设备编号");
            return rspData;
        }
        startPage();
        List<RaData> list = raDevListService.getDevHistoryTable(devSn, strTime, endTime);
        return getDataTable(list);
    }
    @GetMapping(value = "/test")
    public R test(){
        raDevListService.batchRemoveDuplicates();
        return R.ok();
    }
}
