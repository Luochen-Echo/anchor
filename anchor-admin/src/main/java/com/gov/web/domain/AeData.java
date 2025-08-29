package com.gov.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AeData {
    private Long id;
    private String deviceId;       // 设备编号
    private Double amp;           // AMP(dB)
    private Double asl;           // ASL(dB)
    private Double energy;        // 能量(Kpj)
    private Double rms;           // RMS(mv)
    private Integer riseTime;     // 上升时间
    private Integer riseCount;    // 上升计数
    private Integer ringCount;    // 振铃计数
    private Integer duration;     // 持续时间
    private Double timestamp;     // 时间戳
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;      // 创建时间
}