package com.cooltoo;

import com.cooltoo.backend.features.AppFeatures;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.spi.FeatureProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@EnableSwagger2
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    @Value("${togglz_property}")
    private String togglzFilePath;


    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(AppFeatures.class);
    }

    @Bean
    public StateRepository stateRepository() throws IOException {
        File file = null;
        logger.info("get property uri " + togglzFilePath);
        file = new File(togglzFilePath);
        logger.info(file.getAbsolutePath());
        return new FileBasedStateRepository(file);
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        logger.info("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            logger.info(beanName);
        }
    }

}
