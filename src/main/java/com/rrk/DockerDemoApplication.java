package com.rrk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
//@EnableDiscoveryClient
//开启配置文件动态刷新
//@RefreshScope
@EnableTransactionManagement
public class DockerDemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(DockerDemoApplication.class,args);
    }
}
