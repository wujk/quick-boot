package com.wujk.spring.properties;

import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
public class PropertiesConfiguration {

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private List<Resource> common() {
        String[] propertiesFiles = new String[]{};
        List<Resource> resources = new ArrayList<Resource>();
        for (int i = 0; i < propertiesFiles.length; i++) {
            resources.add(resourcePatternResolver.getResource("classpath*:".concat(propertiesFiles[i])));
        }
        return resources;
    }

    /**
     * 该类型激活可用@Value("${redis.maxIdle}")配置
     * @return
     * @throws Exception
     */
    @Bean
    @Profile("dev")
    public PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurerDev() throws Exception {
        PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurer = new PreferencesPlaceholderConfigurer();
        preferencesPlaceholderConfigurer.setProperties(dev());
        return preferencesPlaceholderConfigurer;
    }

    @Bean
    @Profile("sit")
    public PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurerSit() throws Exception {
        PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurer = new PreferencesPlaceholderConfigurer();
        preferencesPlaceholderConfigurer.setProperties(sit());
        return preferencesPlaceholderConfigurer;
    }

    @Bean
    @Profile("prod")
    public PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurerProd() throws Exception {
        PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurer = new PreferencesPlaceholderConfigurer();
        preferencesPlaceholderConfigurer.setProperties(prod());
        return preferencesPlaceholderConfigurer;
    }

    /**
     * 该类型配置激活可用@Value("#{prop['redis.host']}")来注释
     * @return
     * @throws Exception
     */
    @Bean("prop")
    @Profile("dev")
    public Properties dev() throws Exception {
        return getProperties("classpath*:*_dev.properties");
    }

    @Bean("prop")
    @Profile("sit")
    public Properties sit() throws Exception {
        return getProperties("classpath*:*_sit.properties");
    }

    @Bean("prop")
    @Profile("prod")
    public Properties prod() throws Exception {
        return getProperties("classpath*:*_prod.properties");
    }

    private Properties getProperties(String location) throws Exception {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        List<Resource> list = common();
        Resource[] _resources = resourcePatternResolver.getResources(location);
        list.addAll(Arrays.asList(_resources));
        Resource[] resources = new Resource[list.size()];
        resources = list.toArray(resources);
        propertiesFactoryBean.setLocations(resources);
        propertiesFactoryBean.afterPropertiesSet();
        Properties properties = propertiesFactoryBean.getObject();
        return properties;
    }

}
