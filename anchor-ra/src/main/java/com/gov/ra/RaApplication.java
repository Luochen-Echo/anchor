package com.gov.ra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RaApplication {

    public static void main(String[] args) {
        // 先启动Spring容器
        SpringApplication.run(RaApplication.class, args);



       // ConfigurableApplicationContext context = SpringApplication.run(RaApplication.class, args);


    }

}
