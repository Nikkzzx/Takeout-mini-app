package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置类，用于创建AliOssUtil对象
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean//保存对象
    @ConditionalOnMissingBean//当容器中没有这个bean时，创建这个bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);
        log.info("endpoint:{}", aliOssProperties.getEndpoint());
        log.info("bucketName:{}", aliOssProperties.getBucketName());
        log.info("accessKeyId:{}", aliOssProperties.getAccessKeyId());
        log.info("accessKeySecret:{}", aliOssProperties.getAccessKeySecret());
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
