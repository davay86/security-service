package com.security.test.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.security.test.api")
@Import({SecurityConfiguration.class})
public class CloudConfiguration {
}
