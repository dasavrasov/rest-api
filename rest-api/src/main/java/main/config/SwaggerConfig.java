package main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {                  
	
	@Autowired
	private Environment environment;
	
    @Bean
    public Docket api() {
    	
    	String[] profiles= environment.getActiveProfiles();
    	
        Docket docket= new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("vvv.controller"))              
          .paths(PathSelectors.regex("/test.*")) 
          .build()
          .apiInfo(metadata());  

         if (profiles.length>0 && (profiles[0].equals("prod") || profiles[0].equals("test")))
        	 docket.enable(false);
                 
        return docket;
    }
    
    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("API для интеграции с системой")
                .description("Операции для интеграции с системой")
                .version("1.0")
                .build();
    }    
    
    @Bean
    UiConfiguration uiConfig() {
    	
    	UiConfiguration UI=UiConfigurationBuilder.builder()    		  
          .deepLinking(true)
          .displayOperationId(false)
          .defaultModelsExpandDepth(-1)
          .defaultModelExpandDepth(1)
          .defaultModelRendering(ModelRendering.EXAMPLE)
          .displayRequestDuration(false)
          .docExpansion(DocExpansion.NONE)
          .filter(false)
          .maxDisplayedTags(null)
          .operationsSorter(OperationsSorter.METHOD)
          .showExtensions(false)
          .tagsSorter(TagsSorter.ALPHA)
          .supportedSubmitMethods(new String[] {"get","post"})          
          .validatorUrl(null)
          .build();
      
       return UI;
    }    
}