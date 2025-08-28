package com.gov.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gov.web.domain.AeData;
import com.gov.web.domain.RaData;
import com.gov.web.domain.RaDevList;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface RaDevListMapper extends BaseMapper<RaDevList> {
    Integer createTable(@Param("tableName") String tableName);

    int insertInfoBatch(@Param("tableName") String tableName, @Param("list") List<RaData> list);

    List<RaData> queryTable(@Param("tableName") String tableName, @Param("strTime") String strTime, @Param("endTime") String endTime);

    // 分页查询并按时间排序
    @Select("SELECT * FROM `${tableName}` ORDER BY create_time ASC LIMIT #{limit} OFFSET #{offset}")
    List<RaData> selectPageOrderByCreateTime(@Param("tableName") String tableName,@Param("offset") int offset, @Param("limit") int limit);

    // 批量删除指定ID记录
    @Delete("<script>" +
            "DELETE FROM `${tableName}` WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deleteBatchIds(@Param("tableName") String tableName, @Param("ids") List<Long> ids);

    // 重置自增ID
    @Update("ALTER TABLE `${tableName}` MODIFY id INT(11) NOT NULL FIRST,DROP PRIMARY KEY")
    void resetAutoIncrement1(@Param("tableName") String tableName);

    @Update("ALTER TABLE `${tableName}` ADD id2 INT(11) NOT NULL AUTO_INCREMENT FIRST,ADD PRIMARY KEY (id2)")
    void resetAutoIncrement2(@Param("tableName") String tableName);

    @Update("ALTER TABLE `${tableName}` DROP id")
    void resetAutoIncrement3(@Param("tableName") String tableName);

    @Update("ALTER TABLE `${tableName}` CHANGE id2 id INT(11) NOT NULL AUTO_INCREMENT FIRST")
    void resetAutoIncrement4(@Param("tableName") String tableName);


}
