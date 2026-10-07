package com.banking.accounts.kafkaconfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.banking.common.events.constants.KafkaTopics;

@Configuration
public class KafkaTopicConfig {
	
	@Bean
	public NewTopic accountsEventTopic() {
		return TopicBuilder.name(KafkaTopics.ACCOUNT_EVENTS)
				.partitions(3)
				.replicas(1)
				.build();
	}
}
