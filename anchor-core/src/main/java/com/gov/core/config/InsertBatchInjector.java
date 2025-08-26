package com.gov.core.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MyBatis Plus的SQL注入器，继承了DefaultSqlInjector，
 */
@Component
public class InsertBatchInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo){
        List<AbstractMethod> mothodList=super.getMethodList(mapperClass,tableInfo);
        mothodList.add(new InsertBatchSomeColumn());
        return mothodList;
    };
}
