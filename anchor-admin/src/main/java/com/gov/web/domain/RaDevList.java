package com.gov.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 设备列表实体类
 */
@Data
@TableName("ra_dev_list")
@Builder
public class RaDevList {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 添加时间
     */
    private Date createTime;

    /**
     * 传感器编号
     */
    private String deviceCode;

    /**
     * 传感器状态
     */
    private Integer type;

    /**
     * client_num
     */
    private String clientNum;

    /**
     * address
     */
    private String address;

    /**
     * channel
     */
    private String channel;
}