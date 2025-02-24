package com.reminder.penshop.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.reminder.penshop", annotationClass = Mapper.class)
public class MybatisConfiguration {

}
