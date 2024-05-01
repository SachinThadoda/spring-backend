package com.publics.news.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	 @Bean
	    public Docket api() {
	        return new Docket(DocumentationType.SWAGGER_2)
	        		.apiInfo(getApiInfo())
	                .securitySchemes(Arrays.asList(apiKey()))
	                .securityContexts(Arrays.asList(securityContext()))
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.publics.news"))
	                .paths(PathSelectors.any())
	                .build();
	    }

	    private ApiKey apiKey() {
	        return new ApiKey("Token Access", "Authorization", "header");
	    }

	    private SecurityContext securityContext() {
	        return SecurityContext.builder()
	                .securityReferences(Arrays.asList(new SecurityReference("Token Access", new AuthorizationScope[0])))
	                .build();
	    }
	    
	    private ApiInfo getApiInfo() {
	        return new ApiInfoBuilder()
	                .title("User API")
	                .description("User management API")
	                .version("1.0")	
	                .build();
	    }
}
