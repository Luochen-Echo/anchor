package com.gov.web.demo;



import com.gov.web.domain.RaData;
import com.gov.web.service.RaDevListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest
public class RaLogProcessorTest {

    @Autowired
    private RaDevListService raDevListService;

    @Test
    public void processLogAndStoreBySensor() throws IOException {

    }
    public static List<RaData> removeConsecutiveDuplicates(List<RaData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }

        List<RaData> result = new ArrayList<>();
        RaData previous = dataList.get(0);
        result.add(previous);

        for (int i = 1; i < dataList.size(); i++) {
            RaData current = dataList.get(i);

            // 比较value1和value2是否相同
            boolean valuesEqual = (previous.getValue1() == null ? current.getValue1() == null
                    : previous.getValue1().equals(current.getValue1()))
                    && (previous.getValue2() == null ? current.getValue2() == null
                    : previous.getValue2().equals(current.getValue2()));

            if (!valuesEqual) {
                result.add(current);
                previous = current;
            }
        }

        return result;
    }







}