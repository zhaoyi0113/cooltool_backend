package com.cooltoo.config;

import com.cooltoo.backend.api.NurseAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;

/**
 * Created by yzzhao on 3/12/16.
 */
//@Configuration
//@EnableSwagger2
//@ComponentScan(basePackageClasses = {NurseAPI.class})
//@Component
public class SwaggerConfiguration  {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build().pathMapping("/");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("App API")
                .description("App API")
                .version("1.0.0-SNAPSHOT")
                .termsOfServiceUrl("")
                .contact("Cooltoo company")
                .license("Public")
                .licenseUrl("http://cooltoo.com/")

                .build();
    }

    @PostConstruct
    /**
     * Initializes Swagger Configuration
     */
    public void initializeSwaggerConfiguration() {

//        SwaggerConfig swaggerConfig = ConfigFactory.config();
//        swaggerConfig.setBasePath("http://localhost:8080/nursego/nurse");
//        swaggerConfig.setApiVersion( "0.0.1" );
//        ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
//        scanner.setResourcePackage("com.cooltoo.backend.api");
//        ScannerFactory.setScanner((Scanner) scanner);
//        ClassReaders.setReader( new DefaultJaxrsApiReader( ) );
//        BeanConfig beanConfig = new BeanConfig();
//        beanConfig.setVersion("1.0.2");
////        beanConfig.setSchemes(new String[]{"http"});
////        beanConfig.setHost("localhost:8002");
//        beanConfig.setBasePath("/nursego");
//        beanConfig.setResourcePackage("com.cooltoo");
//        beanConfig.setScan(true);


    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
}
