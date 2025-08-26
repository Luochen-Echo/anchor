package com.gov.web.controller;

import com.gov.common.constant.HttpStatus;
import com.gov.common.controller.BaseController;
import com.gov.common.page.TableDataInfo;
import com.gov.web.domain.AeData;
import com.gov.web.service.AeDevListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ae")
public class AeController  extends BaseController {

    @Autowired
    private AeDevListService aeDevListService;

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
        List<AeData> list = aeDevListService.getDevHistoryTable(devSn, strTime, endTime);
        return getDataTable(list);
    }
}
