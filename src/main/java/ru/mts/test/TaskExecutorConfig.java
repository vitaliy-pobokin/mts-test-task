package ru.mts.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TaskExecutorConfig {

    @Bean
    public ExecutorService executor(@Value("${threadPoolSize}") int threadPoolSize) {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
