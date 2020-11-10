package com.rrk.config;

import com.rrk.config.properties.XxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlJobConfig {

    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Autowired
    XxlJobProperties xxlJobProperties;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor  xxlJobExecutor = new XxlJobSpringExecutor ();
        xxlJobExecutor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        xxlJobExecutor.setAppName(xxlJobProperties.getAppName());
        xxlJobExecutor.setIp(xxlJobProperties.getIp());
        xxlJobExecutor.setPort(xxlJobProperties.getPort());
        xxlJobExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobExecutor.setLogPath(xxlJobProperties.getLogPath());
        xxlJobExecutor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());

        return xxlJobExecutor;
    }
}
