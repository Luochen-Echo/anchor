package com.gov.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gov.web.domain.AeData;
import com.gov.web.domain.AeDevList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AeDevListMapper extends BaseMapper<AeDevList> {
    Integer createTable(@Param("tableName") String tableName);
    int insertInfoBatch(@Param("tableName") String tableName, @Param("list")List<AeData> list);
    List<AeData> queryTable(@Param("tableName") String tableName, @Param("strTime") String strTime, @Param("endTime") String endTime);
}
