package com.rrk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
//开启配置文件动态刷新
@RefreshScope
public class DockerDemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(DockerDemoApplication.class,args);
    }
}
