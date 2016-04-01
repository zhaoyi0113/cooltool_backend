package com.cooltoo;

import com.cooltoo.backend.features.AppFeatures;
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
import org.togglz.core.repository.property.PropertyBasedStateRepository;
import org.togglz.core.repository.property.PropertySource;
import org.togglz.core.spi.FeatureProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;


@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@EnableSwagger2
public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(AppFeatures.class);
    }

    @Bean
    public StateRepository stateRepository() throws IOException {
        URL url= getClass().getResource("/application.properties");
        File file = null;
        try {
            file = new File(url.toURI());
            return new FileBasedStateRepository(file);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final InMemoryStateRepository stateRepository = new InMemoryStateRepository();
        stateRepository.setFeatureState(new FeatureState(AppFeatures.SMS_CODE, true));
        return stateRepository;
    }


//    @Bean
//    public BeanConfig beanConfig(){
//        BeanConfig beanConfig = new BeanConfig();
//        beanConfig.setVersion("1.0.2");
//        beanConfig.setSchemes(new String[]{"http"});
//        beanConfig.setHost("localhost:8080");
//        beanConfig.setBasePath("/nursego");
//        beanConfig.setResourcePackage("com.cooltoo");
//        beanConfig.setScan(true);
//        return beanConfig;
//    }

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
