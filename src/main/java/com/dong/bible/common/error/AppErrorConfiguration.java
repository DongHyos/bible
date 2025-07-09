package com.dong.bible.common.error;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for ErrorAttribute(404 error handing)
 * <br/> Required  app.error.use.attributes = true
 */
@ConditionalOnProperty(name = "app.error.use.attributes", havingValue = "true")
@Configuration
@RequiredArgsConstructor
public class AppErrorConfiguration {
    @Bean
    public ErrorAttributes errorAttributes(){
        return new AppErrorAttributes();
    }
}
