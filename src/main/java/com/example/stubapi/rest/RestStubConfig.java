package com.example.stubapi.rest;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RestStubProperties.class)
public class RestStubConfig {
}
