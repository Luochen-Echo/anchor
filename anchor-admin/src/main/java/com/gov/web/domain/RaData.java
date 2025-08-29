package com.gov.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String deviceId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Double value1;
    private Double value2;
}