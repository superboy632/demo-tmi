package com.example.demotmi.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicCreationConfig {

    @Bean
    fun newTopics(): NewTopic {
        return TopicBuilder.name("rule-commands")
            .partitions(3)
            .replicas(1)
            .build()
    }
}