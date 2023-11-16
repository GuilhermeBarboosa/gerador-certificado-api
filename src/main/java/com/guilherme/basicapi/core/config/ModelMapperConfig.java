package com.guilherme.basicapi.core.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper novoModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}