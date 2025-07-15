package com.example.demotmi.kafka

import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.service.RuleService
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
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

    @KafkaListener(topics = ["rule-commands"], groupId = "rule-group")
    override fun consume(record: ConsumerRecord<String, String>) {
        val node = objectMapper.readTree(record.value())
        val type = node.get("type").asText()
        val message = node.get("message")

        when (type) {
            "create" -> {
                val cmd = objectMapper.treeToValue(message, CreateRuleCommand::class.java)
                val request = ruleMapper.toRuleCreateRequest(cmd)
                runBlocking {
                    runCatching {
                        ruleService.create(request)
                    } .onSuccess {
                        logger { info("Successfully $type rule command") }
                    } .onFailure {
                        logger { error("Failed to $type rule command") }
                    }
                }
            }

            "delete" -> {
                val cmd = objectMapper.treeToValue(message, DeleteRuleCommand::class.java)
                runBlocking {
                    runCatching {
                        ruleService.delete(cmd.id)
                    } .onSuccess {
                        logger { info("Successfully $type rule command") }
                    } .onFailure {
                        logger { error("Failed to $type rule command") }
                    }
                }
            }
        }
    }
}