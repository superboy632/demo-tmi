package com.example.demotmi.kafka

import com.example.demotmi.Fixtures
import com.example.demotmi.controller.DefaultExceptionHandler
import com.example.demotmi.kafka.DefaultRuleConsumer.Companion.LISTENER_ID
import com.example.demotmi.mapper.RuleMapper
import com.example.demotmi.service.RuleService
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration

@SpringBootTest(
    properties = [
        "spring.kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    ],
    classes = [
        DefaultRuleConsumer::class,
        KafkaAutoConfiguration::class,
        JacksonAutoConfiguration::class,
    ]
)
@EmbeddedKafka(
    partitions = 1,
    topics = ["rule-commands"],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RuleConsumerTest {

    @MockitoBean
    private lateinit var ruleService: RuleService

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var ruleMapper: RuleMapper

    @Autowired
    protected lateinit var endpointRegistry: KafkaListenerEndpointRegistry

    @BeforeEach
    fun setup() {
        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        val producerFactory = DefaultKafkaProducerFactory<String, String>(producerProps)
        kafkaTemplate = KafkaTemplate(producerFactory)
        kafkaTemplate.defaultTopic = "rule-commands"
    }

    @Test
    fun createTest() =  runTest {
        val message = Fixtures.createKafkaMessage()
        val request = Fixtures.ruleCreateRequest()
        val json = objectMapper.writeValueAsString(message)

        whenever(ruleMapper.toRuleCreateRequest(message.message)).thenReturn(request)
        runBlocking {
            whenever(ruleService.create(request))
                .thenReturn(Fixtures.rule())
        }

        val listener = endpointRegistry.getListenerContainer(LISTENER_ID)
        ContainerTestUtils.waitForAssignment(listener, 1)

        kafkaTemplate.send("rule-commands", json).get()
        await().atMost(Duration.ofSeconds(5))
            .untilAsserted {
                runBlocking {
                    verify(ruleService).create(request)
                }
            }
    }

    @Test
    fun deleteTest() =  runTest {
        val message = Fixtures.deleteKafkaMessage()
        val json = objectMapper.writeValueAsString(message)
        runBlocking {
            whenever(ruleService.delete(message.message.id))
                .thenReturn(mock())
        }

        kafkaTemplate.send("rule-commands", json)
        await().atMost(Duration.ofSeconds(5))
            .untilAsserted {
                runBlocking {
                    verify(ruleService).delete(message.message.id)
                }
            }
    }
}