package com.gov.web.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 历史数据基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaData {
    private Long id;
    private String deviceId;      // 设备编号
    private Date createTime;
    private Double value1;
    private Double value2;
}