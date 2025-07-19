package com.example.demotmi.kafka

import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.service.RuleService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.helpers.Reporter.info
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

fun interface RuleConsumer {

    fun consume(record: ConsumerRecord<String, String>)

}

@Service
class DefaultRuleConsumer (
    private val objectMapper: ObjectMapper,
    private val ruleService: RuleService,
    private val ruleMapper: RuleMapper
) : RuleConsumer {

    @KafkaListener(topics = ["rule-commands"], groupId = "rule-group", id = LISTENER_ID)
    override fun consume(record: ConsumerRecord<String, String>) {
        val node = objectMapper.readTree(record.value())
        val type = node.get("type").asText()

        when (type) {
            "create" -> {
                val message = objectMapper.readValue(record.value(), object: TypeReference<KafkaMessage<CreateRuleCommand>>() {})
                val request = ruleMapper.toRuleCreateRequest(message.message)
                logger.info { "Rule create request: $request" }
                runBlocking {
                    runCatching {
                        ruleService.create(request)
                    } .onSuccess {
                        logger.info { info("Successfully $type rule command") }
                    } .onFailure {
                        logger.info { error("Failed to $type rule command") }
                    }
                }
            }
            "delete" -> {
                val message = objectMapper.readValue(record.value(), object: TypeReference<KafkaMessage<DeleteRuleCommand>>() {})
                runBlocking {
                    runCatching {
                        ruleService.delete(message.message.id)
                    } .onSuccess {
                        logger.info { info("Successfully $type rule command") }
                    } .onFailure {
                        logger.info { error("Failed to $type rule command") }
                    }
                }
            }
        }
    }
    companion object : KLogging() {
        const val LISTENER_ID = "task-feedback-consumer"
    }
}