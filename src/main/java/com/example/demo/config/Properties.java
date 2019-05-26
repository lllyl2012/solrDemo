package com.example.demo.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "yiyun")
@Data
public class Properties {


    private String prod;

    private String classPath;

    @Bean
    public PaginationInterceptor pageHelper(){
        PaginationInterceptor pageHelper = new PaginationInterceptor();
        pageHelper.setDialectType("mysql");
        return pageHelper;
    }

}
