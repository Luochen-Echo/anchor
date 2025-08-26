package com.gov.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ae_dev_list
 */
@TableName(value ="ae_dev_list")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AeDevList implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String deviceId;

    /**
     * 
     */
    private String remark;

    private Date createTime;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;





}