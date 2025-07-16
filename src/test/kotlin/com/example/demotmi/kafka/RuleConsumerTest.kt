package com.example.demotmi.kafka

import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.persistence.Rule
import com.example.demotmi.persistence.RuleRepository
import com.example.demotmi.request.RuleCreateRequest
import com.example.demotmi.service.RuleService
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.util.UUID

@EmbeddedKafka(partitions = 1, topics = ["rule-commands"], brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@SpringBootTest
class RuleConsumerTest {

    @MockitoBean
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var ruleRepository: RuleRepository

    @MockitoBean
    private lateinit var ruleMapper: RuleMapper

    @BeforeEach
    fun setup() {
        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        val producerFactory = DefaultKafkaProducerFactory<String, String>(producerProps)
        kafkaTemplate = KafkaTemplate(producerFactory)
        kafkaTemplate.defaultTopic = "rule-commands"
    }

    @Test
    fun createTest() =  runTest {
        val command = CreateRuleCommand(agentId = 123, agentType = "test")
        val message = KafkaMessage(
            type = "create",
            message = command,
        )
        val request = RuleCreateRequest(
            agentRuleId = 123,
            agentType = "test",
            agentId = 123,
            deviceName = "test",
            deviceId = 42,
            deviceAddress = "test",
        )
        val json = objectMapper.writeValueAsString(message)
        whenever(ruleMapper.toRuleCreateRequest(command)).thenReturn(request)
        runBlocking {
            whenever(ruleService.create(request))
                .thenReturn(Rule(UUID.randomUUID(),
                    123,
                    "string",
                    123,
                    null,
                    null,
                    null,
                    true))
        }

        kafkaTemplate.send("rule-commands", json)
        await().atMost(Duration.ofSeconds(2))
            .untilAsserted {
                runBlocking {
                    verify(ruleService).create(request)
                }
            }
    }

    @Test
    fun deleteTest() =  runTest {
        val command = DeleteRuleCommand(id = UUID.randomUUID(), agentType = "test")
        val message = KafkaMessage(
            type = "delete",
            message = command,
        )

        val json = objectMapper.writeValueAsString(message)
        runBlocking {
            whenever(ruleService.delete(message.message.id))
                .thenReturn(mock())
        }

        kafkaTemplate.send("rule-commands", json)
        await().atMost(Duration.ofSeconds(2))
            .untilAsserted {
                runBlocking {
                    verify(ruleService).delete(message.message.id)
                }
            }
    }
}