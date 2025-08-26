package com.gov.common.page;

import com.gov.common.constant.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author ruoyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;


    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
        this.code= HttpStatus.SUCCESS;
        this.msg="操作成功";
    }


}
